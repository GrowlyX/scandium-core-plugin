package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.manager.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerManager serverManager = CorePlugin.getInstance().getServerManager();
        String displayName = ((sender instanceof Player) ? ((Player) sender).getDisplayName() : ChatColor.DARK_RED + "Console");

        if (!sender.hasPermission("scandium.command.mutechat")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            serverManager.setChatEnabled(!serverManager.isChatEnabled());

            Bukkit.broadcastMessage(ChatColor.GREEN + "The chat has been " + (serverManager.isChatEnabled() ? "enabled" : "disabled") + " by " + displayName + ".");
            CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + displayName + " &bhas " + (serverManager.isChatEnabled() ? "enabled" : "disabled") + " chat.");
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("-s")) {
                serverManager.setChatEnabled(!serverManager.isChatEnabled());

                Bukkit.broadcastMessage(ChatColor.RED + "The chat has been " + (serverManager.isChatEnabled() ? "enabled" : "disabled") + " by staff.");
                CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + displayName + " &bhas " + (serverManager.isChatEnabled() ? "enabled" : "disabled") + " chat.");
            }
        }

        return false;
    }
}
