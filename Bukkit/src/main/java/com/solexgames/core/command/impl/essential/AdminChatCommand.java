package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
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

public class AdminChatCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission(ChatChannelType.ADMIN.getPermission())) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            if (potPlayer.getChannel() == null || !potPlayer.getChannel().equals(ChatChannelType.ADMIN)) {
                potPlayer.setChannel(ChatChannelType.ADMIN);
                player.sendMessage(ChatColor.GREEN + Color.translate("You've entered the admin chat channel."));
            } else {
                potPlayer.setChannel(null);
                player.sendMessage(ChatColor.RED + ("You've exited the admin chat channel."));
            }
        }

        if (args.length > 0) {
            final String message = StringUtil.buildMessage(args, 0);
            RedisUtil.publishAsync(RedisUtil.onChatChannel(ChatChannelType.ADMIN, message, player));
        }
        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("ac");
    }
}
