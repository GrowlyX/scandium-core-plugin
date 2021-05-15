package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "clear", aliases = {"ci", "clearinv", "clearinventory"})
public class ClearCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.clear")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.getInventory().clear();
            player.sendMessage(Color.SECONDARY_COLOR + "You've cleared your inventory.");

            PlayerUtil.sendAlert(player, "cleared inventory");
        }
        if (args.length == 1) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                player.getInventory().clear();

                player.sendMessage(Color.SECONDARY_COLOR + "You've cleared " + target.getDisplayName() + Color.SECONDARY_COLOR + "'s inventory.");

                PlayerUtil.sendAlert(player, "cleared inventory for " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }
        return false;
    }
}
