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

public class StaffChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission(ChatChannelType.STAFF.getPermission())) {
            if (args.length == 0) {
                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                if (potPlayer.getChannel() == null || !potPlayer.getChannel().equals(ChatChannelType.STAFF)) {
                    potPlayer.setChannel(ChatChannelType.STAFF);
                    player.sendMessage(Color.translate("&aYou have entered the staff chat channel."));
                } else {
                    potPlayer.setChannel(null);
                    player.sendMessage(Color.translate("&cYou have exited the staff chat channel."));
                }
            }
            if (args.length > 0) {
                String message = StringUtil.buildMessage(args, 0);
                CorePlugin.getInstance().getRedisThread().execute(() -> client.write(RedisUtil.onChatChannel(ChatChannelType.STAFF, message, player)));
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
