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

public class UnMuteCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.unmute")) {
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
                                    if (punishment.getPunishmentType() == PunishmentType.MUTE) {
                                        punishment.setRemoved(true);
                                        punishment.setRemovalReason(message.replace("-s", ""));
                                        punishment.setRemover(player.getUniqueId());
                                        punishment.setActive(false);

                                        if (message.endsWith("-s")) {
                                            Bukkit.getOnlinePlayers().forEach(player1 -> {
                                                if (player1.hasPermission("scandium.staff")) {
                                                    player1.sendMessage(Color.translate(
                                                            "&7[Silent] " + offlinePlayer.getName() + " &awas " + "un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (player != null ? player.getDisplayName() : "&4CONSOLE") + "&a."
                                                    ));
                                                }
                                            });
                                        } else {
                                            Bukkit.broadcastMessage(Color.translate(
                                                    offlinePlayer.getName() + " &awas " + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (player != null ? player.getDisplayName() : "&4CONSOLE") + "&a."
                                            ));
                                        }

                                        punishment.savePunishment();
                                        Punishment.getAllPunishments().remove(punishment);
                                        Punishment.getAllPunishments().add(punishment);
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
        return false;
    }
}
