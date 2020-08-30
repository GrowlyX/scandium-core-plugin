package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "report"))
public class ReportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (args.length == 0) {

            player.sendMessage(CC.translate("&cUsage: /report <player> <message>"));

            return true;
        }

        if (args.length == 1) {

            player.sendMessage(CC.translate("&cUsage: /report <player> <message>"));

            return true;

        }

        if (args.length == 2) {

            String cheater = args[0];
            String reason = args[1];

            player.sendMessage(CC.translate(Messages.string("FORMAT.REPORT") + Messages.string("MESSAGES.REPORT")));

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.hasPermission("core.staff")) {
                    target.sendMessage(CC.translate(Messages.string("FORMAT.REPORT") + Messages.string("MESSAGES.REPORT-STAFF")
                            .replace("<sender>", sender.getName())
                            .replace("<cheater>", cheater)
                            .replace("<reason>", reason)
                    ));
                }
            }

            return true;

        }


        return false;
    }
}