package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "sudoall", aliases = "sda", permission = "core.commands.sudoall"))
public class SudoAllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;

        if (args.length == 0) {

            sender.sendMessage(CC.translate("&cUsage: /sudoall <message>"));

            return true;
        }

        if (args.length == 1) {

            String moose = args[0];

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.chat(label);
            }

            sender.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&7Forced all players to say :&6" + moose));
        }


        return false;
    }
}