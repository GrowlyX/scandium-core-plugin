package com.solexgames.core.command.impl.essential;

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

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.tppos")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length <= 2) {
            player.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <x> <y> <z>.");
        }
        if (args.length == 3) {
            try {
                int x1 = Integer.parseInt(args[0]);
                int y1 = Integer.parseInt(args[1]);
                int z1 = Integer.parseInt(args[2]);

                player.teleport(new Location(player.getWorld(), x1, y1, z1, 0.0F, 0.0F));
                player.sendMessage(serverType.getSecondaryColor() + "Teleported you to " + serverType.getMainColor() + x1 + serverType.getSecondaryColor() + ", " + serverType.getMainColor() + y1 + serverType.getSecondaryColor() + ", " + serverType.getMainColor() + z1 + serverType.getSecondaryColor() + ".");

                StaffUtil.sendAlert(player, "teleported to " + x1 + ", " + y1 + ", " + z1);
            } catch (Exception e) {
                player.sendMessage(Color.translate("&cOne of those values was not an integer."));
            }
        }
        return false;
    }
}
