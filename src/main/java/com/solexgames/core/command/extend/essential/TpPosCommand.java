package com.solexgames.core.command.extend.essential;

import com.solexgames.core.command.BaseCommand;
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
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.tppos")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <x> <y> <z>."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    player.sendMessage(Color.translate("&cUsage: /" + label + " <x> <y> <z>."));
                }
                if (args.length == 2) {
                    player.sendMessage(Color.translate("&cUsage: /" + label + " <x> <y> <z>."));
                }
                if (args.length == 3) {
                    try {
                        int x1 = Integer.parseInt(args[0]);
                        int y1 = Integer.parseInt(args[1]);
                        int z1 = Integer.parseInt(args[2]);

                        player.teleport(new Location(player.getWorld(), x1, y1, z1, 0.0F, 0.0F));
                        player.sendMessage(ChatColor.GREEN + "Teleported you to " + x1 + ", " + y1 + ", " + z1 + ".");

                        StaffUtil.sendAlert(player, "teleported to " + x1 + ", " + y1 + ", " + z1);
                    } catch (Exception e) {
                        player.sendMessage(Color.translate("&cTry again!"));
                    }
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
