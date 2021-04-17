package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffAnnounceCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!player.hasPermission("scandium.command.staffannounce")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <message>."));
        }
        if (args.length > 0) {
            String message = StringUtil.buildMessage(args, 0);
            CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&b[S] &3[Alert]&f: &b" + message);
        }

        return false;
    }
}
