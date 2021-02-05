package vip.potclub.core.command.extend.punish;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.StringUtil;

import java.util.UUID;

public class UnBanCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <reason> [-s]."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <reason> [-s]."));
                }
                if (args.length > 1) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    String message = StringUtil.buildMessage(args, 1);
                    if (offlinePlayer != null) {
                        Punishment.getAllPunishments().forEach(punishment -> {
                            if (punishment.getTarget().equals(offlinePlayer.getUniqueId())) {
                                if (punishment.isActive()) {
                                    if (punishment.getPunishmentType() == PunishmentType.BAN) {
                                        punishment.setRemoved(true);
                                        punishment.setRemovalReason(message.replace("-s", ""));
                                        punishment.setRemover(UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670"));
                                        punishment.setRemoverName("Console");
                                        punishment.setActive(false);

                                        if (message.endsWith("-s")) {
                                            Bukkit.getOnlinePlayers().forEach(player1 -> {
                                                if (player1.hasPermission("scandium.staff")) {
                                                    player1.sendMessage(Color.translate(
                                                            "&7[Silent] " + offlinePlayer.getName() + " &awas " + "un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4Console&a."
                                                    ));
                                                }
                                            });
                                        } else {
                                            Bukkit.broadcastMessage(Color.translate(
                                                    "&7" + offlinePlayer.getName() + " &awas " + "un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4Console&a."
                                            ));
                                        }

                                        punishment.savePunishment();
                                    }
                                }
                            }
                        });
                    } else {
                        sender.sendMessage(Color.translate("&cThat player does not exist."));
                    }
                }
                return false;
            }
        } else {
            Player player = (Player) sender;
            if (player.hasPermission("scandium.command.unban")) {
                if (args.length == 0) {
                    player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <reason> [-s]."));
                }
                if (args.length > 0) {
                    if (args.length == 1) {
                        player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <reason> [-s]."));
                    }
                    if (args.length > 1) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                        String message = StringUtil.buildMessage(args, 1);
                        if (offlinePlayer != null) {
                            Punishment.getAllPunishments().forEach(punishment -> {
                                if (punishment.getTarget().equals(offlinePlayer.getUniqueId())) {
                                    if (punishment.isActive()) {
                                        if (punishment.getPunishmentType() == PunishmentType.BAN) {
                                            punishment.setRemoved(true);
                                            punishment.setRemovalReason(message.replace("-s", ""));
                                            punishment.setRemover(player.getUniqueId());
                                            punishment.setActive(false);
                                            punishment.setRemoverName(player.getName());

                                            if (message.endsWith("-s")) {
                                                Bukkit.getOnlinePlayers().forEach(player1 -> {
                                                    if (player1.hasPermission("scandium.staff")) {
                                                        player1.sendMessage(Color.translate(
                                                                "&7[Silent] " + offlinePlayer.getName() + " &awas " + "un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + player.getDisplayName() + "&a."
                                                        ));
                                                    }
                                                });
                                            } else {
                                                Bukkit.broadcastMessage(Color.translate(
                                                        "&7" + offlinePlayer.getName() + " &awas un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + player.getDisplayName() + "&a."
                                                ));
                                            }

                                            punishment.savePunishment();
                                        }
                                    }
                                }
                            });
                        } else {
                            player.sendMessage(Color.translate("&cThat player does not exist."));
                        }
                    }
                }
            } else {
                player.sendMessage(Color.translate("&cNo permission."));
            }
        }

        return false;
    }
}
