package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManagerChatCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission(ChatChannelType.MANAGER.getPermission())) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            if (potPlayer.getChannel() == null || !potPlayer.getChannel().equals(ChatChannelType.MANAGER)) {
                potPlayer.setChannel(ChatChannelType.MANAGER);
                player.sendMessage(ChatColor.GREEN + Color.translate("You've entered the manager chat channel."));
            } else {
                potPlayer.setChannel(null);
                player.sendMessage(ChatColor.RED + ("You've exited the manager chat channel."));
            }
        }

        if (args.length > 0) {
            String message = StringUtil.buildMessage(args, 0);
            RedisUtil.writeAsync(RedisUtil.onChatChannel(ChatChannelType.MANAGER, message, player));
        }

        return false;
    }
}
