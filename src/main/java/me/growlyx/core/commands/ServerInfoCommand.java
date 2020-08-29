package me.growlyx.core.commands;

import me.growlyx.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "serverinfo", aliases = "info"))
public class ServerInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            if (args.length == 0) {
                Player player = (Player) sender;

                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate("&6&lCore &7- &fServer Info"));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate("&fName&7: &6&l" + player.getDisplayName()));
                player.sendMessage(CC.translate("&fVersion&7: &6&l" + Bukkit.getServer().getVersion()));
                player.sendMessage(CC.translate("&fPlayers&7: &6&l" + Bukkit.getServer().getOnlinePlayers().size()));
                player.sendMessage(CC.translate("&fMax Players&7: &6&l" + Bukkit.getServer().getMaxPlayers()));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));


            } else {

                System.out.println("Only players can execute this command.");

            }

            return false;
        }

        return false;
    }
}
