package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "heal", permission = "core.commands.heal"))
public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                player.setHealth(20);
                player.setFoodLevel(20);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.HEAL")));

                return true;
            }

            if (args.length == 1) {

                Player healing = Bukkit.getPlayer(args[0]);

                healing.setHealth(20);
                healing.setFoodLevel(20);

                healing.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.HEAL")));

                return true;
            }

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}