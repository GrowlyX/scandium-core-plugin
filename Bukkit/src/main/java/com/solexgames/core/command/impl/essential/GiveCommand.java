package com.solexgames.core.command.impl.essential;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Command(label = "give", permission = "scandium.command.give")
public class GiveCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(Color.translate(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player> <amount> <item>"));
        }
        if (args.length > 2) {
            final Player target = Bukkit.getPlayer(args[0]);

            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
                player.sendMessage(ChatColor.RED + ("Error: That amount is not an integer."));
                return false;
            }

            final String message = StringUtil.buildMessage(args, 2);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                return false;
            }

            final String formatted = message.replace(" ", "_").toUpperCase();
            final XMaterial material = XMaterial.matchXMaterial(formatted).orElse(null);

            if (material == null) {
                player.sendMessage(ChatColor.RED + ("Error: That material does not exist."));
                return false;
            }

            final ItemStack itemStack = material.parseItem();

            if (itemStack != null) {
                itemStack.setAmount(amount);

                target.getInventory().setItemInHand(itemStack);
            }

            player.sendMessage(Color.SECONDARY_COLOR + "You've given " + target.getDisplayName() + Color.MAIN_COLOR + " " + amount + Color.MAIN_COLOR + " " + StringUtils.capitalize(material.name().toLowerCase().replace("_", " ")) + Color.SECONDARY_COLOR + "!");
        }

        return false;
    }
}
