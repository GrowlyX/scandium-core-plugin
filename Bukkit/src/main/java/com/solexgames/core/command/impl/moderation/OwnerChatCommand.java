package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "ownerchat", aliases = {"oc"})
public class OwnerChatCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission(ChatChannelType.OWNER.getPermission())) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            if (potPlayer.getChannel() == null || !potPlayer.getChannel().equals(ChatChannelType.OWNER)) {
                potPlayer.setChannel(ChatChannelType.OWNER);
                player.sendMessage(ChatColor.GREEN + Color.translate("You've entered the owner chat channel."));
            } else {
                potPlayer.setChannel(null);
                player.sendMessage(ChatColor.RED + ("You've exited the owner chat channel."));
            }
        }

        if (args.length > 0) {
            final String message = StringUtil.buildMessage(args, 0);
            RedisUtil.publishAsync(RedisUtil.onChatChannel(ChatChannelType.OWNER, message, player));
        }
        return false;
    }
}
