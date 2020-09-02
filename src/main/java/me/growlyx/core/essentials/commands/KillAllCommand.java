package me.growlyx.core.essentials.commands;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "killall", aliases = {"ka"}, permission = "core.command.killall"))
public class KillAllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            if (args.length == 0) {

                Player player = (Player) sender;

                for (Player players: Bukkit.getOnlinePlayers()) {
                    players.setHealth(0);
                    players.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&cYou were killed!"));
                }

            }

            return false;
        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}
