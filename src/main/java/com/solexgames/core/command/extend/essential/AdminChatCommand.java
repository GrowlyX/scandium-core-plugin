package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission(ChatChannelType.ADMIN.getPermission())) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <message>."));
            }

            if (args.length > 0) {
                String message = StringUtil.buildMessage(args, 0);
                CorePlugin.getInstance().getRedisThread().execute(() -> client.write(RedisUtil.onChatChannel(ChatChannelType.ADMIN, message, player)));
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
