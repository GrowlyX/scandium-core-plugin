package com.solexgames.core.command.extend.network;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceUpdateCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        if (!sender.hasPermission("scandium.command.forceupdate")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        RedisUtil.writeAsync(RedisUtil.onServerUpdate());
        sender.sendMessage(ChatColor.GREEN + "Force-updated the server.");
        return false;
    }
}
