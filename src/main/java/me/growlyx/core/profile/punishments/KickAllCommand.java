package me.growlyx.core.profile.punishments;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "kickall", permission = "core.commands.kickall"))
public class KickAllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (args.length == 0) {

            player.sendMessage(CC.translate("&cUsage: /kickall <message>"));

            return true;
        }

        if (args.length == 1) {

            String msg = "";

            for (int i = 0; i< args.length;i++) {
                msg = msg + args[0] + " ";
            }

            for (Player players : Bukkit.getOnlinePlayers()) {
                players.kickPlayer(CC.translate("&cYou were kicked! Reason: " + msg));
            }

            return true;

        }

        return false;
    }
}