package com.solexgames.core.command.extend.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
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

        Bukkit.broadcastMessage(Color.translate("&aThe chat has been cleared by " + (sender instanceof Player ? sender.getDisplayName() : "&4Console") + "&a."));
        return false;
    }
}
