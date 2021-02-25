package com.solexgames.core.command.extend.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpHereCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.tphere")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player>."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target != null) {
                        target.teleport(player.getLocation());
                        player.sendMessage(Color.translate("&aTeleported " + target.getDisplayName() + "&a to your location."));
                        target.sendMessage(Color.translate("&aYou have been teleported to " + target.getDisplayName() + "&a."));

                        StaffUtil.sendAlert(player, "teleported " + target.getName() + " to themselves");
                    } else {
                        player.sendMessage(Color.translate("&cThat player does not exist."));
                    }
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}