package com.solexgames.core.command.impl.essential;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Command(label = "maxitem", aliases = "more")
public class MaxItemCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.maxitem")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        final ItemStack itemStack = player.getItemInHand();

        if (itemStack != null && !itemStack.getType().equals(XMaterial.AIR.parseMaterial())) {
            itemStack.setAmount(itemStack.getMaxStackSize());
            player.updateInventory();
        } else {
            player.sendMessage(ChatColor.RED + "Error: You aren't holding an item in your hand right now.");
        }

        return false;
    }
}
