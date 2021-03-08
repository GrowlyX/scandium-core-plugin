package com.solexgames.core.command.extend.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnMuteCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <reason> &7[-s]."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <reason> &7[-s]."));
                }
                if (args.length > 1) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    String message = StringUtil.buildMessage(args, 1);
                    if (offlinePlayer != null) {
                        Punishment.getAllPunishments()
                                .stream()
                                .filter(punishment -> punishment.getTarget().equals(offlinePlayer.getUniqueId()))
                                .filter(Punishment::isActive)
                                .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.MUTE))
                                .forEach(punishment -> {
                                    punishment.setRemoved(true);
                                    punishment.setRemovalReason(message.replace("-s", ""));
                                    punishment.setRemover(UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670"));
                                    punishment.setRemoverName("Console");
                                    punishment.setActive(false);

                                    if (message.endsWith("-s")) {
                                        Bukkit.getOnlinePlayers().forEach(player1 -> {
                                            if (player1.hasPermission("scandium.staff")) {
                                                player1.sendMessage(Color.translate(
                                                        "&7[S] " + offlinePlayer.getName() + " &awas " + "un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4Console&a."
                                                ));
                                            }
                                        });
                                    } else {
                                        Bukkit.broadcastMessage(Color.translate(
                                                "&7" + offlinePlayer.getName() + " &awas " + "un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4Console&a."
                                        ));
                                    }

                                    punishment.savePunishment();

                                    RedisUtil.writeAsync(RedisUtil.removePunishment(null, punishment, message));
                                });
                    } else {
                        sender.sendMessage(Color.translate("&cThat player does not exist."));
                    }
                }
                return false;
            }
        } else {
            Player player = (Player) sender;
            if (player.hasPermission("scandium.command.unmute")) {
                if (args.length == 0) {
                    sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <reason> &7[-s]."));
                }
                if (args.length > 0) {
                    if (args.length == 1) {
                        sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <reason> &7[-s]."));
                    }
                    if (args.length > 1) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                        String message = StringUtil.buildMessage(args, 1);
                        if (offlinePlayer != null) {
                            Punishment.getAllPunishments()
                                    .stream()
                                    .filter(punishment -> punishment.getTarget().equals(offlinePlayer.getUniqueId()))
                                    .filter(Punishment::isActive)
                                    .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.MUTE))
                                    .forEach(punishment -> {
                                        punishment.setRemoved(true);
                                        punishment.setRemovalReason(message.replace("-s", ""));
                                        punishment.setRemover(player.getUniqueId());
                                        punishment.setActive(false);
                                        punishment.setRemoverName(player.getName());

                                        if (message.endsWith("-s")) {
                                            Bukkit.getOnlinePlayers().forEach(player1 -> {
                                                if (player1.hasPermission("scandium.staff")) {
                                                    player1.sendMessage(Color.translate(
                                                            "&7[S] " + offlinePlayer.getName() + " &awas " + "un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + player.getDisplayName() + "&a."
                                                    ));
                                                }
                                            });
                                        } else {
                                            Bukkit.broadcastMessage(Color.translate(
                                                    "&7" + offlinePlayer.getName() + " &awas un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + player.getDisplayName() + "&a."
                                            ));
                                        }

                                        punishment.savePunishment();

                                        RedisUtil.writeAsync(RedisUtil.removePunishment(player, punishment, message));
                                    });
                        } else {
                            player.sendMessage(Color.translate("&cThat player does not exist."));
                        }
                    }
                }
            } else {
                player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
            }
        }

        return false;
    }
}
