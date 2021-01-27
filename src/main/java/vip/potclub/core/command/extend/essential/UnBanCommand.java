package vip.potclub.core.command.extend.essential;

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

public class UnBanCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.unban")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <reason>."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <reason>."));
                }
                if (args.length == 2) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    String message = args[1];
                    if (offlinePlayer != null) {
                        Punishment.getAllPunishments().forEach(punishment -> {
                            if (punishment.getTarget().equals(offlinePlayer.getUniqueId())) {
                                if (punishment.isActive()) {
                                    if (punishment.getPunishmentType().equals(PunishmentType.BAN)) {
                                        punishment.setRemoved(true);
                                        punishment.setRemovalReason(message);
                                        punishment.setRemover(player.getUniqueId());
                                    }
                                }
                            }
                        });
                        player.sendMessage(Color.translate("&aUnbanned " + args[0] + "!"));
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
