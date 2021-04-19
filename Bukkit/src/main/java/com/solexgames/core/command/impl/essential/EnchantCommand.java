package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnchantCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.enchant")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        player.openEnchanting(player.getLocation(), true);

        return false;
    }
}