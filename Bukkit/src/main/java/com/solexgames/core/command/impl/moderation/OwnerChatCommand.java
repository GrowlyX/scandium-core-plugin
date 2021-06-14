package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.Constants;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "ownerchat", permission = "scandium.channels.owner", aliases = {"oc"})
public class OwnerChatCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (args.length == 0) {
            if (potPlayer.getChannel() == null || !potPlayer.getChannel().equals(ChatChannelType.OWNER)) {
                potPlayer.setChannel(ChatChannelType.OWNER);
                player.sendMessage(Constants.STAFF_PREFIX + ChatColor.GREEN + "You're now chatting in the " + ChatColor.BLUE + "Owner" + ChatColor.GREEN + " channel.");
            } else {
                potPlayer.setChannel(null);
                player.sendMessage(Constants.STAFF_PREFIX + ChatColor.RED + "You're no longer chatting in the " + ChatColor.BLUE + "Owner" + ChatColor.RED + " channel.");
            }
        }

        if (args.length > 0) {
            final String message = StringUtil.buildMessage(args, 0);
            RedisUtil.publishAsync(RedisUtil.onChatChannel(ChatChannelType.OWNER, message, player));
        }
        return false;
    }
}
