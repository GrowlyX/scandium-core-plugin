package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@Command(label = "sudoall")
public class SudoAllCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.sudoall")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.translate(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " [c:] [e:] <message>."));
        }

        if (args.length > 0) {
            final String message = StringUtil.buildMessage(args, 0);

            if (message.contains("/sudoall") || message.contains("/scandium:sudoall") || message.contains("e:sudoall") || message.contains("e:scandium:sudoall")) {
                sender.sendMessage(ChatColor.RED + "Error: That message contains a blacklisted phrase.");
                return false;
            }

            if (message.startsWith("c:")) {
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.chat(message.replace("c:", "")));
                sender.sendMessage(Color.SECONDARY_COLOR + "Made all online players chat '" + Color.MAIN_COLOR + message.replace("c:", "") + Color.SECONDARY_COLOR + "'.");
            } else if (message.startsWith("e:")) {
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.performCommand(message.replace("e:", "")));
                sender.sendMessage(Color.SECONDARY_COLOR + "Made all online players execute '" + Color.MAIN_COLOR + message.replace("e:", "") + Color.SECONDARY_COLOR + "'.");
            } else {
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.chat(message));
                sender.sendMessage(Color.SECONDARY_COLOR + "Made all online players chat '" + Color.MAIN_COLOR + message + Color.SECONDARY_COLOR + "'.");
            }
        }
        return false;
    }
}
