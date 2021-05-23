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

import java.util.*;
import java.util.stream.Collectors;

@Command(label = "blacklist", permission = "scandium.command.blacklist")
public class BlacklistCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
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
                    final UUID playerId = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(document.getString("name"));
                    final List<Punishment> punishmentList = Punishment.getAllPunishments().stream()
                            .filter(Objects::nonNull)
                            .filter(Punishment::isActive)
                            .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.BLACKLIST))
                            .filter(punishment -> punishment.getTarget().equals(playerId))
                            .sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed())
                            .collect(Collectors.toList());

                    if (punishmentList.size() > 0) {
                        sender.sendMessage(ChatColor.RED + "Error: That player already has an active blacklist!");
                    } else {
                        final Date newIssuingDate = new Date();
                        final UUID newPunishmentUuid = UUID.randomUUID();
                        final String newPunishmentId = SaltUtil.getRandomSaltedString(7);

                        final String targetName = args[0];
                        final UUID targetUuid = UUID.fromString(document.getString("uuid"));
                        final String reason = StringUtil.buildMessage(args, 1);

                        final String issuerName = (sender instanceof Player ? ((Player) sender).getName() : "Console");
                        final String issuerNameNull = (sender instanceof Player ? ((Player) sender).getName() : null);
                        final UUID issuerUuid = (sender instanceof Player ? ((Player) sender).getUniqueId() : null);

                        final boolean isSilent = reason.endsWith("-s");

                        final Punishment punishment = new Punishment(
                                PunishmentType.BLACKLIST,
                                issuerUuid,
                                targetUuid,
                                issuerName,
                                reason.replace("-s", ""),
                                newIssuingDate,
                                0L,
                                true,
                                newIssuingDate,
                                newPunishmentUuid,
                                newPunishmentId,
                                true
                        );

                        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(targetName);

                        CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, issuerNameNull, document, isSilent);

                        if (potPlayer != null) {
                            potPlayer.getPunishments().add(punishment);
                            potPlayer.saveWithoutRemove();
                        }

                        RedisUtil.publishAsync(RedisUtil.executePunishment(
                                PunishmentType.BLACKLIST,
                                issuerUuid,
                                targetUuid,
                                issuerName,
                                reason.replace("-s", ""),
                                newIssuingDate,
                                0L,
                                true,
                                newIssuingDate,
                                newPunishmentUuid,
                                newPunishmentId,
                                false
                        ));
                    }
                }
            });
        }

        return false;
    }
}
