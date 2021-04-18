package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MaxItemCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.maxitem")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        ItemStack itemStack = player.getItemInHand();

        if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
            itemStack.setAmount(itemStack.getMaxStackSize());
            player.updateInventory();
        } else {
            player.sendMessage(ChatColor.RED + "Error: You aren't holding an item in your hand right now.");
        }

        return false;
    }
}
