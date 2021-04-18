package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftingCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.crafting")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        player.openWorkbench(player.getLocation(), true);

        return false;
    }
}
