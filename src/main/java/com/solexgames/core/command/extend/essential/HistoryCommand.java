package com.solexgames.core.command.extend.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.menu.extend.punish.history.PunishHistoryViewMainMenu;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
