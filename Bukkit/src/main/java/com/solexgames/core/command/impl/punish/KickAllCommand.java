package com.solexgames.core.command.impl.punish;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@Command(label = "kickall")
public class KickAllCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.punishments.kickall")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        Bukkit.getOnlinePlayers().forEach(target -> target.kickPlayer("You were kicked."));
        sender.sendMessage(ChatColor.GREEN + Color.translate("Kicked all online players."));

        return false;
    }
}
