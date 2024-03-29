package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "tphere", permission = "scandium.command.tphere", aliases = "s")
public class TpHereCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!sender.hasPermission("scandium.command.tphere")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>");
        }
        if (args.length == 1) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                target.teleport(player.getLocation());

                player.sendMessage(Color.SECONDARY_COLOR + "Teleported " + target.getDisplayName() + Color.SECONDARY_COLOR + " to your location.");
                target.sendMessage(Color.SECONDARY_COLOR + "You've been teleported to " + target.getDisplayName() + Color.SECONDARY_COLOR + ".");

                PlayerUtil.sendAlert(player, "teleported " + target.getName() + " to themselves");
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        return false;
    }
}
