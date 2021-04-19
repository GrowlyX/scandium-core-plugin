package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlowChatCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        String displayName = ((sender instanceof Player) ? ((Player) sender).getDisplayName() : ChatColor.DARK_RED + "Console");
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!sender.hasPermission("scandium.command.slowchat")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <time> " + ChatColor.GRAY + "(Use 0 to disable slow chat)");
        }

        if (args.length == 1) {
            try {
                int time = Integer.parseInt(args[0]);
                CorePlugin.getInstance().getServerManager().setChatSlow(time * 1000L);
                Bukkit.broadcastMessage(CorePlugin.getInstance().getServerManager().getChatSlow() > 0L ? ChatColor.GREEN + "Public chat is now in slow mode. " + ChatColor.GRAY + "(" + time + " seconds)" : ChatColor.RED + "Public chat is no longer in slow mode.");

                CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + displayName + " &bhas slowed the chat to &e" + time + " seconds&b.");
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error: That's not a valid number.");
            }
        }
        return false;
    }
}
