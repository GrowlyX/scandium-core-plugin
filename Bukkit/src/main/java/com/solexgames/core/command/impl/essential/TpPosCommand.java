package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(label = "tppos", permission = "scandium.command.tppos")
public class TpPosCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.tppos")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length <= 2) {
            player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <x> <y> <z>");
        }
        if (args.length == 3) {
            try {
                final int x1 = Integer.parseInt(args[0]);
                final int y1 = Integer.parseInt(args[1]);
                final int z1 = Integer.parseInt(args[2]);

                player.teleport(new Location(player.getWorld(), x1, y1, z1, 0.0F, 0.0F));
                player.sendMessage(Color.SECONDARY_COLOR + "Teleported you to " + Color.MAIN_COLOR + x1 + Color.SECONDARY_COLOR + ", " + Color.MAIN_COLOR + y1 + Color.SECONDARY_COLOR + ", " + Color.MAIN_COLOR + z1 + Color.SECONDARY_COLOR + ".");

                PlayerUtil.sendAlert(player, "teleported to " + x1 + ", " + y1 + ", " + z1);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + ("One of those values was not an integer."));
            }
        }
        return false;
    }
}
