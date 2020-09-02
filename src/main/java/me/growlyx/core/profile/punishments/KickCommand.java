package me.growlyx.core.profile.punishments;

import me.growlyx.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "kick", permission = "core.commands.kick"))
public class KickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (args.length == 0) {

            player.sendMessage(CC.translate("&cUsage: /kick <player> <message>"));

            return true;
        }

        if (args.length == 1) {

            player.sendMessage(CC.translate("&cUsage: /kick <player> <message>"));

            return true;
        }

        if (args.length == 2) {


            Player target = Bukkit.getPlayer(args[1]);
            String msg = "";

            for (int i = 0; i< args.length;i++) {
                msg = msg + args[1] + " ";
            }


            target.kickPlayer(CC.translate("&cYou were kicked! Reason: " + msg));


            return true;

        }


        return false;
    }
}