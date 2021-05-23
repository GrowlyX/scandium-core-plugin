package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "enderchest", aliases = "echest", permission = "scandium.command.echest")
public class EnderChestCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            player.openInventory(player.getEnderChest());
        }

        if (args.length == 1) {
            if (!player.hasPermission("scandium.command.echest.other")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }

            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                player.openInventory(target.getEnderChest());
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        return false;
    }
}
