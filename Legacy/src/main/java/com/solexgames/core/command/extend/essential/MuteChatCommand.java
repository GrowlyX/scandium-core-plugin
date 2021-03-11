package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.mutechat")) {
            if (args.length == 0) {
                if (CorePlugin.getInstance().getServerManager().isChatEnabled()) {
                    CorePlugin.getInstance().getServerManager().setChatEnabled(false);
                    Bukkit.broadcastMessage(Color.translate("&aThe chat has been disabled by " + player.getDisplayName() + "&a."));
                    CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + player.getDisplayName() + " &bhas disabled global chat&b.");
                } else if (!CorePlugin.getInstance().getServerManager().isChatEnabled()) {
                    CorePlugin.getInstance().getServerManager().setChatEnabled(true);
                    Bukkit.broadcastMessage(Color.translate("&aThe chat has been enabled by " + player.getDisplayName() + "&a."));
                    CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + player.getDisplayName() + " &bhas enabled global chat&b.");
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
