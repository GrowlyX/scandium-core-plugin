package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "kill", permission = "core.commands.kill"))
public class KillCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                player.setHealth(0D);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.KILL")));

                return true;
            }

            if (args.length == 1) {

                Player killed = Bukkit.getPlayer(args[0]);

                player.setHealth(0D);

                killed.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.KILL")));

                return true;
            }

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}