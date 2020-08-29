package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

import java.util.Iterator;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "helpop", aliases = "request"))
public class HelpOPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (args.length == 0) {

            player.sendMessage(CC.translate("&cUsage: /helpop <message>"));

            return true;
        }

        if (args.length == 1) {

            String moose = args[0];

            String msg = "";

            for (int i = 0; i< args.length;i++) {
                msg = msg + args[0] + " ";
            }

            player.sendMessage(CC.translate(Messages.string("FORMAT.HELPOP") + Messages.string("MESSAGES.HELPOP")));

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.hasPermission("core.staff")) {
                    target.sendMessage(CC.translate(Messages.string("FORMAT.HELPOP") + Messages.string("MESSAGES.HELPOP-STAFF")
                            .replace("<user>", sender.getName())
                            .replace("<msg>", msg)
                    ));
                }
            }

            return true;

        }


        return false;
    }
}