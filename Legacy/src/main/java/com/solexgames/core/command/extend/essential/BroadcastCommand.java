package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BroadcastCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.broadcast")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " [l:] [g:] <message>."));
            }

            if (args.length > 0) {
                String message = StringUtil.buildMessage(args, 0);
                if (message.startsWith("l:")) {
                    Bukkit.broadcastMessage(Color.translate(message));
                } else if (message.startsWith("g:")) {
                    CorePlugin.getInstance().getRedisThread().execute(() -> client.write(RedisUtil.onGlobalBroadcast(message)));
                } else {
                    CorePlugin.getInstance().getRedisThread().execute(() -> client.write(RedisUtil.onGlobalBroadcast(message)));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
