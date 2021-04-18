package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.clearchat")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        for (int lines = 0; lines < 250; lines++) {
            Bukkit.broadcastMessage(Color.translate("  "));
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + Color.translate("The chat has been cleared by " + (sender instanceof Player ? ((Player) sender).getDisplayName() : "&4Console") + ChatColor.GREEN + "."));

        return false;
    }
}
