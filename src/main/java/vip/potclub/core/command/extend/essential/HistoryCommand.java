package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.menu.extend.punish.history.PunishHistoryViewMainMenu;
import vip.potclub.core.menu.extend.punish.history.PunishHistoryViewSubMenu;
import vip.potclub.core.util.Color;

public class HistoryCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.history")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player>."));
            }
            if (args.length > 0) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (offlinePlayer != null) {
                    new PunishHistoryViewMainMenu(player, offlinePlayer.getName()).open(player);
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist in our databases."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
