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

public class BlacklistCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.mute")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (args.length < 2) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <reason> " + ChatColor.GRAY + "[-s]" + ChatColor.WHITE + ".");
        }
        if (args.length >= 2) {
            Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByName(args[0]).orElse(null);

            if (document == null) {
                sender.sendMessage(ChatColor.RED + "That player does not exist in our database.");
                return false;
            }

            UUID playerId = UUIDUtil.fetchUUID(document.getString("name"));
            List<Punishment> punishmentList = Punishment.getAllPunishments().stream()
                    .filter(Objects::nonNull)
                    .filter(Punishment::isActive)
                    .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.BLACKLIST))
                    .filter(punishment -> punishment.getTarget().equals(playerId))
                    .sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed())
                    .collect(Collectors.toList());

            if (punishmentList.size() > 0) {
                sender.sendMessage(ChatColor.RED + "That player already has an active blacklist!");
                return false;
            }

            Date newIssuingDate = new Date();
            UUID newPunishmentUuid = UUID.randomUUID();
            String newPunishmentId = SaltUtil.getRandomSaltedString(7);

            String targetName = args[0];
            UUID targetUuid = UUID.fromString(document.getString("uuid"));
            String reason = StringUtil.buildMessage(args, 1);

            String issuerName = (sender instanceof Player ? ((Player) sender).getName() : "Console");
            Player issuerPlayer = (sender instanceof Player ? ((Player) sender) : null);
            UUID issuerUuid = (sender instanceof Player ? ((Player) sender).getUniqueId() : null);

            boolean isSilent = reason.endsWith("-s");

            try {
                Punishment punishment = new Punishment(
                        PunishmentType.BLACKLIST,
                        issuerUuid,
                        targetUuid,
                        issuerName,
                        reason.replace("-s", ""),
                        newIssuingDate,
                        newIssuingDate.getTime() - DateUtil.parseDateDiff("1d", false),
                        true,
                        newIssuingDate,
                        newPunishmentUuid,
                        newPunishmentId,
                        true
                );
                punishment.savePunishment();

                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(targetName);

                CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, issuerPlayer, document, isSilent);

                if (potPlayer != null) {
                    potPlayer.getPunishments().add(punishment);
                    potPlayer.saveWithoutRemove();
                }

                RedisUtil.writeAsync(RedisUtil.executePunishment(
                        PunishmentType.BLACKLIST,
                        issuerUuid,
                        targetUuid,
                        issuerName,
                        reason.replace("-s", ""),
                        newIssuingDate,
                        newIssuingDate.getTime() - DateUtil.parseDateDiff("1d", false),
                        true,
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
