package com.solexgames.core.command.impl.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BanCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.ban")) {
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
                    UUID playerId = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(document.getString("name"));
                    List<Punishment> punishmentList = Punishment.getAllPunishments().stream()
                            .filter(Objects::nonNull)
                            .filter(Punishment::isActive)
                            .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.BAN))
                            .filter(punishment -> punishment.getTarget().equals(playerId))
                            .sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed())
                            .collect(Collectors.toList());

                    if (punishmentList.size() > 0) {
                        sender.sendMessage(ChatColor.RED + "Error: That player already has an active ban!");
                    } else {
                        Date newIssuingDate = new Date();
                        UUID newPunishmentUuid = UUID.randomUUID();
                        String newPunishmentId = SaltUtil.getRandomSaltedString(7);

                        String targetName = args[0];
                        UUID targetUuid = UUID.fromString(document.getString("uuid"));
                        String reason = StringUtil.buildMessage(args, 2);

                        String issuerName = (sender instanceof Player ? ((Player) sender).getName() : "Console");
                        String issuerNameNull = (sender instanceof Player ? ((Player) sender).getName() : null);
                        UUID issuerUuid = (sender instanceof Player ? ((Player) sender).getUniqueId() : null);

                        boolean isPermanent = (args[1].equalsIgnoreCase("perm") || args[1].equalsIgnoreCase("permanent"));
                        boolean isSilent = reason.endsWith("-s");

                        try {
                            Punishment punishment = new Punishment(
                                    PunishmentType.BAN,
                                    issuerUuid,
                                    targetUuid,
                                    issuerName,
                                    reason.replace("-s", ""),
                                    newIssuingDate,
                                    newIssuingDate.getTime() - DateUtil.parseDateDiff(args[1], false),
                                    isPermanent,
                                    newIssuingDate,
                                    newPunishmentUuid,
                                    newPunishmentId,
                                    true
                            );
                            punishment.savePunishment();

                            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(targetName);
                            if (potPlayer != null) {
                                potPlayer.getPunishments().add(punishment);
                                potPlayer.saveWithoutRemove();
                            }

                            CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, issuerNameNull, document, isSilent);

                            RedisUtil.publishAsync(RedisUtil.executePunishment(
                                    PunishmentType.BAN,
                                    issuerUuid,
                                    targetUuid,
                                    issuerName,
                                    reason.replace("-s", ""),
                                    newIssuingDate,
                                    newIssuingDate.getTime() - DateUtil.parseDateDiff(args[1], false),
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

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }
}
