package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <message>."));
        }
        if (args.length > 0) {
            String message = StringUtil.buildMessage(args, 0);
            if (potPlayer.isCanReceiveDms()) {
                if (potPlayer.getLastRecipient() != null) {
                    if (potPlayer.getLastRecipient().isOnline()) {
                        StringUtil.sendPrivateMessage(player, potPlayer.getLastRecipient(), message);
                    } else {
                        player.sendMessage(Color.translate("&cThat player is not online."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cYou don't have an ongoing conversation with anyone."));
                }
            } else {
                player.sendMessage(Color.translate("&cYou have your dms disabled."));
            }
        }
        return false;
    }
}
