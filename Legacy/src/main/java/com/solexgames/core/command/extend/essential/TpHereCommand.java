package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpHereCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor secondColor = serverType.getSecondaryColor();
        Player player = (Player) sender;

        if (!sender.hasPermission("scandium.command.tphere")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>."));
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                target.teleport(player.getLocation());
                player.sendMessage(Color.translate(secondColor + "Teleported " + target.getDisplayName() + secondColor + " to your location."));
                target.sendMessage(Color.translate(secondColor + "You have been teleported to " + target.getDisplayName() + secondColor + "."));

                StaffUtil.sendAlert(player, "teleported " + target.getName() + " to themselves");
            } else {
                player.sendMessage(Color.translate("&cThat player does not exist."));
            }
        }

        return false;
    }
}
