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

public class HostChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission(ChatChannelType.HOST.getPermission())) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            if (potPlayer.getChannel() == null || !potPlayer.getChannel().equals(ChatChannelType.HOST)) {
                potPlayer.setChannel(ChatChannelType.HOST);
                player.sendMessage(Color.translate("&aYou have entered the host chat channel."));
            } else {
                potPlayer.setChannel(null);
                player.sendMessage(Color.translate("&cYou have exited the host chat channel."));
            }
        }

        if (args.length > 0) {
            String message = StringUtil.buildMessage(args, 0);
            RedisUtil.writeAsync(RedisUtil.onChatChannel(ChatChannelType.HOST, message, player));
        }
        return false;
    }
}
