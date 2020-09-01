package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "list", aliases = {"who", "people", "peeps", "men", "woman"}))
public class ListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                player.sendMessage(CC.translate("&7&m-------------------------------------------"));
                // Add Vault Support + display ranks & prefixes here
                player.sendMessage(CC.translate("&7(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ") &f" + Bukkit.getOnlinePlayers()));
                player.sendMessage(CC.translate("&7&m-------------------------------------------"));
            }

            return true;

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}