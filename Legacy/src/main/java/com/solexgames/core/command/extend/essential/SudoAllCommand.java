package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoAllCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = serverType.getMainColor();
        ChatColor secondColor = serverType.getSecondaryColor();

        if (!sender.hasPermission("scandium.command.sudoall")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " [c:] [e:] <message>."));
        }

        if (args.length > 0) {
            String message = StringUtil.buildMessage(args, 0);

            if (message.contains("/sudoall") || message.contains("/scandium:sudoall")) {
                sender.sendMessage(ChatColor.RED + "You cannot perform this action right now.");
                return false;
            }

            if (message.startsWith("c:")) {
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.chat(message.replace("c:", "")));
                sender.sendMessage(Color.translate(secondColor + "Made all online players chat '" + mainColor + message.replace("c:", "") + secondColor + "'."));
            } else if (message.startsWith("e:")) {
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.performCommand(message.replace("e:", "")));
                sender.sendMessage(Color.translate(secondColor + "Made all online players execute '" + mainColor + message.replace("e:", "") + secondColor + "'."));
            } else {
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.chat(message));
                sender.sendMessage(Color.translate(secondColor + "Made all online players chat '" + mainColor + message + secondColor + "'."));
            }
        }
        return false;
    }
}
