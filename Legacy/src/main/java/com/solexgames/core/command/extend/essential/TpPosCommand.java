package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpPosCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = network.getMainColor();
        ChatColor secondColor = network.getSecondaryColor();
        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.tppos")) {
            ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
            if (args.length == 0) {
                player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <x> <y> <z>."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <x> <y> <z>."));
                }
                if (args.length == 2) {
                    player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <x> <y> <z>."));
                }
                if (args.length == 3) {
                    try {
                        int x1 = Integer.parseInt(args[0]);
                        int y1 = Integer.parseInt(args[1]);
                        int z1 = Integer.parseInt(args[2]);

                        player.teleport(new Location(player.getWorld(), x1, y1, z1, 0.0F, 0.0F));
                        player.sendMessage(secondColor + "Teleported you to " + mainColor + x1 + secondColor + ", " + mainColor + y1 + secondColor + ", " + mainColor + z1 + secondColor + ".");

                        StaffUtil.sendAlert(player, "teleported to " + x1 + ", " + y1 + ", " + z1);
                    } catch (Exception e) {
                        player.sendMessage(Color.translate("&cOne of those values was not an integer."));
                    }
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
