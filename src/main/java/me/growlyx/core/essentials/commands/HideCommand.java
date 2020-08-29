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

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "hide", permission = "core.commands.hide"))
public class HideCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                player.sendMessage(CC.translate("&cUsage: /hide <player>"));

                return true;
            }

            if (args.length == 1) {

                Player target = Bukkit.getPlayer(args[0]);

                player.hidePlayer(target);
                player.sendMessage(Messages.string("FORMAT.PREFIX") + "&6" + target.getName() + "&7is now invisible to you.");

                return true;
            }

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}