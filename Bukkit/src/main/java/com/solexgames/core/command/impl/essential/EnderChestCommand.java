package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChestCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.echest")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.openInventory(player.getEnderChest());
        }

        if (args.length == 1) {
            if (!player.hasPermission("scandium.command.echest.other")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                player.openInventory(target.getEnderChest());
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        return false;
    }
}
