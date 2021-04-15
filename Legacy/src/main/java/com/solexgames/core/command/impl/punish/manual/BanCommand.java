package com.solexgames.core.command.impl.punish.manual;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BanCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.ban")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (args.length < 3) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <time> <reason> " + ChatColor.GRAY + "[-s]" + ChatColor.WHITE + ".");
        }
        if (args.length >= 3) {
            AtomicReference<Document> document = new AtomicReference<>();
            CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                document.set(CorePlugin.getInstance().getPlayerManager().getDocumentByName(args[0]).orElse(null));
                completableFuture.complete(true);
            });

            completableFuture.thenRunAsync(() -> {
                if (document.get() == null) {
                    sender.sendMessage(ChatColor.RED + "Error: That player does not exist in our database.");
                } else {
                    UUID playerId = UUIDUtil.fetchUUID(document.get().getString("name"));
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
                        UUID targetUuid = UUID.fromString(document.get().getString("uuid"));
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

                            CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, issuerNameNull, document.get(), isSilent);

                            RedisUtil.writeAsync(RedisUtil.executePunishment(
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
                            ignored.printStackTrace();
                        }
                    }
                }
            });
        }

        return false;
    }
}
