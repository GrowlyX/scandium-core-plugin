package com.solexgames.core.command.impl.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.*;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BlacklistCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.mute")) {
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
                    UUID playerId = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(document.getString("name"));
                    List<Punishment> punishmentList = Punishment.getAllPunishments().stream()
                            .filter(Objects::nonNull)
                            .filter(Punishment::isActive)
                            .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.BLACKLIST))
                            .filter(punishment -> punishment.getTarget().equals(playerId))
                            .sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed())
                            .collect(Collectors.toList());

                    if (punishmentList.size() > 0) {
                        sender.sendMessage(ChatColor.RED + "Error: That player already has an active blacklist!");
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

                            CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, issuerNameNull, document, isSilent);

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
