package com.solexgames.core.command.impl.test;

import com.solexgames.core.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        if (!sender.getName().equals("GrowlyX")) {
            sender.sendMessage(ChatColor.RED + "Nothing to see here! You can move on now ^-^");
            return false;
        }

        return false;
    }
}
