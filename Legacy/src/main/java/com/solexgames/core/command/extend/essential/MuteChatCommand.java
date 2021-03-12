package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerManager serverManager = CorePlugin.getInstance().getServerManager();
        String displayName = ((sender instanceof Player) ? sender.getDisplayName() : ChatColor.DARK_RED + "Console");

        if (!sender.hasPermission("scandium.command.mutechat")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "The chat has been " + (serverManager.isChatEnabled() ? "disabled" : "enabled") + " by " + displayName + ".");
        CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + displayName + " &bhas " + (serverManager.isChatEnabled() ? "enabled" : "disabled") + " chat.");

        return false;
    }
}
