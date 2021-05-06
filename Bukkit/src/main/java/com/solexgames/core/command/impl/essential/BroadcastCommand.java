package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "broadcast", aliases = {"bc", "alert", "galert", "rgalert"})
public class BroadcastCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.broadcast")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " [l:] [g:] <message>.");
        }
        if (args.length > 0) {
            String message = StringUtil.buildMessage(args, 0);
            if (message.startsWith("l:")) {
                Bukkit.broadcastMessage(Color.translate(message.replace("l:", "")));
            } else if (message.startsWith("g:")) {
                RedisUtil.publishAsync(RedisUtil.onGlobalBroadcast(message.replace("g:", "")));
            } else {
                RedisUtil.publishAsync(RedisUtil.onGlobalBroadcast(message));
            }
        }

        return false;
    }
}
