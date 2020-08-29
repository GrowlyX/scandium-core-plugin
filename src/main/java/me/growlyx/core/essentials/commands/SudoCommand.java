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

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "sudo", permission = "core.commands.sudo"))
public class SudoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;

        if (args.length == 0) {

            sender.sendMessage(CC.translate("&CUsage: /sudo <player> <message>"));
            return false;

        }

        if (args.length == 1) {

            sender.sendMessage(CC.translate("&CUsage: /sudo <player> <message>"));
            return false;

        }

        if (args.length == 2) {

            String message = args[1];
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(CC.translate("&cPlayer Not Found!"));
            }

            target.chat(label);
            sender.sendMessage(Messages.string("FORMAT.PREFIX") + ChatColor.GREEN + "Forced target to chat!");

            return true;

        }

        return false;
    }
}