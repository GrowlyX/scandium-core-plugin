package me.growlyx.core.essentials.commands;

import me.growlyx.core.API;
import me.growlyx.core.utils.BukkitReflection;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.Style;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "ping"))
public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&7Your Ping: " + Style.colorPing(BukkitReflection.getPing(player))));
                return true;
            }

            if (args.length == 1) {

                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&cA player with that name could not be found."));
                } else {
                    player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&6" + target.getDisplayName() + "&7's Ping: " + Style.colorPing(BukkitReflection.getPing(target))));
                }

                return true;
            }

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}