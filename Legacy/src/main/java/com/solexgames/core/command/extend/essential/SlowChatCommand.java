package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlowChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String displayName = ((sender instanceof Player) ? ((Player) sender).getDisplayName() : ChatColor.DARK_RED + "Console");
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!sender.hasPermission("scandium.command.slowchat")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <time> " + ChatColor.GRAY + "(Use 0 to disable slow chat)");
        }

        if (args.length == 1) {
            try {
                int time = Integer.parseInt(args[0]);
                CorePlugin.getInstance().getServerManager().setChatSlow(time * 1000L);
                Bukkit.broadcastMessage(CorePlugin.getInstance().getServerManager().getChatSlow() > 0L ? ChatColor.GREEN + "Public chat is now in slow mode. " + ChatColor.GRAY + "(" + time + " seconds)" : ChatColor.RED + "Public chat is no longer in slow mode.");

                CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + displayName + " &bhas slowed the chat to &6" + time + " seconds&b.");
            } catch (NumberFormatException e) {
                sender.sendMessage(Color.translate("&cThat number is invalid."));
            }
        }
        return false;
    }
}
