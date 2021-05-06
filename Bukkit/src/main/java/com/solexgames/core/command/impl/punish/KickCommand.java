package com.solexgames.core.command.impl.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Command(label = "kick")
public class KickCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.kick")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player> <reason> " + ChatColor.GRAY + "[-s]" + ChatColor.WHITE + ".");
        }
        if (args.length >= 2) {
            final UUID uuid = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(args[0]);

            if (uuid == null) {
                sender.sendMessage(org.bukkit.ChatColor.RED + "Error: That player is not valid.");
                return false;
            }

            CorePlugin.getInstance().getPlayerManager().findOrMake(args[0], uuid).thenAcceptAsync(document -> {
                if (document == null) {
                    sender.sendMessage(ChatColor.RED + "Error: That player does not exist in our database.");
                } else {
                    Date newIssuingDate = new Date();
                    UUID newPunishmentUuid = UUID.randomUUID();
                    String newPunishmentId = SaltUtil.getRandomSaltedString(7);

                    String targetName = args[0];
                    UUID targetUuid = UUID.fromString(document.getString("uuid"));
                    String reason = StringUtil.buildMessage(args, 1);

                    String issuerName = (sender instanceof Player ? ((Player) sender).getName() : "Console");
                    String issuerNameNull = (sender instanceof Player ? ((Player) sender).getName() : null);
                    UUID issuerUuid = (sender instanceof Player ? ((Player) sender).getUniqueId() : null);

                    boolean isSilent = reason.endsWith("-s");

                    try {
                        Punishment punishment = new Punishment(
                                PunishmentType.KICK,
                                issuerUuid,
                                targetUuid,
                                issuerName,
                                reason.replace("-s", ""),
                                newIssuingDate,
                                newIssuingDate.getTime() - DateUtil.parseDateDiff("1d", false),
                                false,
                                newIssuingDate,
                                newPunishmentUuid,
                                newPunishmentId,
                                true
                        );
                        punishment.savePunishment();

                        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(targetName);

                        CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, issuerNameNull, document, isSilent);

                        if (potPlayer != null) {
                            potPlayer.getPunishments().add(punishment);
                            potPlayer.saveWithoutRemove();
                        }

                        RedisUtil.publishAsync(RedisUtil.executePunishment(
                                PunishmentType.KICK,
                                issuerUuid,
                                targetUuid,
                                issuerName,
                                reason.replace("-s", ""),
                                newIssuingDate,
                                newIssuingDate.getTime() - DateUtil.parseDateDiff("1d", false),
                                false,
                                newIssuingDate,
                                newPunishmentUuid,
                                newPunishmentId,
                                false
                        ));
                    } catch (Exception ignored) {
                        sender.sendMessage(ChatColor.RED + "Error: That is not a valid duration!");
                        ignored.printStackTrace();
                    }
                }
            });
        }

        return false;
    }
}
