package com.solexgames.core.command.extend.grant;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.menu.extend.grant.GrantHistoryViewMenu;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantsCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.grants")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player>."));
            }
            if (args.length > 0) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    new GrantHistoryViewMenu(player, target).open(player);
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}