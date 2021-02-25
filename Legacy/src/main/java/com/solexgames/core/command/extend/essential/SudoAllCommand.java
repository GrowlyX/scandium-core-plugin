package com.solexgames.core.command.extend.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoAllCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.sudoall")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " [c:] [e:] <message>."));
            }

            if (args.length > 0) {
                String message = StringUtil.buildMessage(args, 0);
                if (message.startsWith("c:")) {
                    Bukkit.getOnlinePlayers().forEach(player1 -> player1.chat(message));
                    player.sendMessage(Color.translate("&aMade all online players chat '" + message + "&a'."));
                } else if (message.startsWith("e:")) {
                    Bukkit.getOnlinePlayers().forEach(player1 -> player1.performCommand(message));
                    player.sendMessage(Color.translate("&aMade all online players execute '" + message + "&a'."));
                } else {
                    Bukkit.getOnlinePlayers().forEach(player1 -> player1.chat(message));
                    player.sendMessage(Color.translate("&aMade all online players chat '" + message + "&a'."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
