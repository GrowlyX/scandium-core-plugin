package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "mutechat", permission = "scandium.command.mutechat")
public class MuteChatCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        final ServerManager serverManager = CorePlugin.getInstance().getServerManager();
        final String displayName = ((sender instanceof Player) ? ((Player) sender).getDisplayName() : ChatColor.DARK_RED + "Console");

        serverManager.setChatEnabled(!serverManager.isChatEnabled());

        final boolean silent = args[0] != null && args[0].equals("-s");
        final String broadcast = silent ? ChatColor.RED + "The chat has been " + (serverManager.isChatEnabled() ? "enabled" : "disabled") + " by " + ChatColor.BOLD + "Staff" + ChatColor.RED + "." : ChatColor.GREEN + "The chat has been " + (serverManager.isChatEnabled() ? "enabled" : "disabled") + " by " + displayName + ".";

        Bukkit.broadcastMessage(broadcast);
        CorePlugin.getInstance().getPlayerManager().sendToNetworkStaffFormatted(displayName + ChatColor.AQUA + " has " + (serverManager.isChatEnabled() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.AQUA + " chat.");

        return false;
    }
}
