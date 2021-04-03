package com.solexgames.core.command.impl.punish.manual;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.DateUtil;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.SaltUtil;
import com.solexgames.core.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class KickCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.kick")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (args.length < 2) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <reason> " + ChatColor.GRAY + "[-s]" + ChatColor.WHITE + ".");
        }
        if (args.length >= 2) {
            AtomicReference<Document> document = new AtomicReference<>(CorePlugin.getInstance().getPlayerManager().getDocumentByName(args[0]).orElse(null));
            CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                document.set(CorePlugin.getInstance().getPlayerManager().getDocumentByName(args[0]).orElse(null));
                completableFuture.complete(true);
            });


            completableFuture.thenRun(() -> {
                if (document.get() == null) {
                    sender.sendMessage(ChatColor.RED + "That player does not exist in our database.");
                } else {
                    Date newIssuingDate = new Date();
                    UUID newPunishmentUuid = UUID.randomUUID();
                    String newPunishmentId = SaltUtil.getRandomSaltedString(7);

                    String targetName = args[0];
                    UUID targetUuid = UUID.fromString(document.get().getString("uuid"));
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

                        CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, issuerNameNull, document.get(), isSilent);

                        if (potPlayer != null) {
                            potPlayer.getPunishments().add(punishment);
                            potPlayer.saveWithoutRemove();
                        }

                        RedisUtil.writeAsync(RedisUtil.executePunishment(
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
                        sender.sendMessage(ChatColor.RED + "That's not a valid duration!");
                        ignored.printStackTrace();
                    }
                }
            });
        }

        return false;
    }
}
