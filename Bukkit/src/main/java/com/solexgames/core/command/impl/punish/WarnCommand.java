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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Command(label = "warn", permission = "scandium.command.warn")
public class WarnCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.warn")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length < 3) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player> <time> <reason> " + ChatColor.GRAY + "[-s]" + ChatColor.WHITE + ".");
        }
        if (args.length >= 3) {
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
                            .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.WARN))
                            .filter(punishment -> punishment.getTarget().equals(playerId))
                            .sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed())
                            .collect(Collectors.toList());

                    if (punishmentList.size() > 0) {
                        sender.sendMessage(ChatColor.RED + "Error: That player already has an active warn!");
                    } else {
                        final Date newIssuingDate = new Date();
                        final UUID newPunishmentUuid = UUID.randomUUID();
                        final String newPunishmentId = SaltUtil.getRandomSaltedString(7);

                        final String targetName = args[0];
                        final UUID targetUuid = UUID.fromString(document.getString("uuid"));

                        String reason = StringUtil.buildMessage(args, 2);

                        final String issuerName = (sender instanceof Player ? ((Player) sender).getName() : "Console");
                        final String issuerNameNull = (sender instanceof Player ? ((Player) sender).getName() : null);
                        final UUID issuerUuid = (sender instanceof Player ? ((Player) sender).getUniqueId() : null);

                        final long dateDiff = DateUtil.parseDateDiff(args[1], false);

                        final boolean isPermanent = (args[1].equalsIgnoreCase("perm") || args[1].equalsIgnoreCase("permanent") || dateDiff == -1L);
                        final boolean isSilent = reason.endsWith("-s");

                        if (dateDiff == -1) {
                            reason = StringUtil.buildMessage(args, args[1].equalsIgnoreCase("perm") || args[1].equalsIgnoreCase("permanent") ? 2 : 1);
                        }

                        try {
                            Punishment punishment = new Punishment(
                                    PunishmentType.WARN,
                                    issuerUuid,
                                    targetUuid,
                                    issuerName,
                                    reason.replace(" -s", ""),
                                    newIssuingDate,
                                    newIssuingDate.getTime() - dateDiff,
                                    isPermanent,
                                    newIssuingDate,
                                    newPunishmentUuid,
                                    newPunishmentId,
                                    true
                            );

                            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(targetName);
                            if (potPlayer != null) {
                                potPlayer.getPunishments().add(punishment);
                                potPlayer.saveWithoutRemove();
                            }

                            CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, issuerNameNull, document, isSilent);

                            RedisUtil.publishAsync(RedisUtil.executePunishment(
                                    PunishmentType.WARN,
                                    issuerUuid,
                                    targetUuid,
                                    issuerName,
                                    reason.replace(" -s", ""),
                                    newIssuingDate,
                                    newIssuingDate.getTime() - dateDiff,
                                    isPermanent,
                                    newIssuingDate,
                                    newPunishmentUuid,
                                    newPunishmentId,
                                    false
                            ));
                        } catch (Exception ignored) {
                            sender.sendMessage(ChatColor.RED + "Error: That is not a valid duration!");
                        }
                    }
                }
            });
        }

        return false;
    }
}
