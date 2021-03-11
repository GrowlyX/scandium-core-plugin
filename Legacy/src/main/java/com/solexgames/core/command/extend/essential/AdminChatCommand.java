package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.player.PotPlayer;
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
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission(ChatChannelType.ADMIN.getPermission())) {
            if (args.length == 0) {
                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                if (potPlayer.getChannel() == null || !potPlayer.getChannel().equals(ChatChannelType.ADMIN)) {
                    potPlayer.setChannel(ChatChannelType.ADMIN);
                    player.sendMessage(Color.translate("&aYou have entered the admin chat channel."));
                } else {
                    potPlayer.setChannel(null);
                    player.sendMessage(Color.translate("&cYou have exited the admin chat channel."));
                }
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
