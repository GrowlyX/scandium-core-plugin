package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.PlayerUtil;
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
            player.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>.");
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                target.teleport(player.getLocation());

                player.sendMessage(secondColor + "Teleported " + target.getDisplayName() + secondColor + " to your location.");
                target.sendMessage(secondColor + "You've been teleported to " + target.getDisplayName() + secondColor + ".");

                PlayerUtil.sendAlert(player, "teleported " + target.getName() + " to themselves");
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }

        return false;
    }
}
