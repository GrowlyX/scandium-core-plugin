package com.solexgames.core.command.extend.punish.manual;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.*;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class MuteCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.mute")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (args.length < 3) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <time> <reason> " + ChatColor.GRAY + "[-s]" + ChatColor.WHITE + ".");
        }
        if (args.length >= 3) {
            Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByName(args[0]).orElse(null);

            if (document == null) {
                sender.sendMessage(ChatColor.RED + "That player does not exist in our database.");
                return false;
            }

            UUID playerId = UUIDUtil.fetchUUID(document.getString("name"));
            List<Punishment> punishmentList = Punishment.getAllPunishments().stream()
                    .filter(Objects::nonNull)
                    .filter(Punishment::isActive)
                    .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.MUTE))
                    .filter(punishment -> punishment.getTarget().equals(playerId))
                    .sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed())
                    .collect(Collectors.toList());

            if (punishmentList.size() > 0) {
                sender.sendMessage(ChatColor.RED + "That player already has an active mute!");
            }

            Date newIssuingDate = new Date();
            UUID newPunishmentUuid = UUID.randomUUID();
            String newPunishmentId = SaltUtil.getRandomSaltedString(7);

            String targetName = args[0];
            UUID targetUuid = UUID.fromString(document.getString("uuid"));
            String reason = StringUtil.buildMessage(args, 2);

            String issuerName = (sender instanceof Player ? ((Player) sender).getName() : "Console");
            String issuerPlayer = (sender instanceof Player ? ((Player) sender).getName() : null);
            UUID issuerUuid = (sender instanceof Player ? ((Player) sender).getUniqueId() : null);

            boolean isPermanent = (args[1].equalsIgnoreCase("perm") || args[1].equalsIgnoreCase("permanent"));
            boolean isSilent = reason.endsWith("-s");

            try {
                Punishment punishment = new Punishment(
                        PunishmentType.WARN,
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

                CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, issuerPlayer, targetName, isSilent);

                if (potPlayer != null) {
                    potPlayer.getPunishments().add(punishment);
                    potPlayer.saveWithoutRemove();
                }

                RedisUtil.writeAsync(RedisUtil.executePunishment(
                        PunishmentType.WARN,
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
                sender.sendMessage(ChatColor.RED + "That's not a valid duration!");
            }
        }

        return false;
    }
}
