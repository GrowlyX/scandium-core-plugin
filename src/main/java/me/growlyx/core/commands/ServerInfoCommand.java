package me.growlyx.core.commands;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "serverinfo", aliases = {"info", "information"}))
public class ServerInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            if (args.length == 0) {

                Player player = (Player) sender;

                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate(Messages.string("FORMAT.MAIN-COLOR") + "&l" + Messages.string("SERVER-NAME") + " &7- &fServer Info"));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate("&fName&7: " + Messages.string("FORMAT.MAIN-COLOR") + player.getDisplayName()));
                player.sendMessage(CC.translate("&fVersion&7: " + Messages.string("FORMAT.MAIN-COLOR") + Bukkit.getServer().getVersion()));
                player.sendMessage(CC.translate("&fPlayers&7: " + Messages.string("FORMAT.MAIN-COLOR") + Bukkit.getOnlinePlayers().size()));
                player.sendMessage(CC.translate("&fMax Players&7: " + Messages.string("FORMAT.MAIN-COLOR") + Bukkit.getMaxPlayers()));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));

            } else {

                System.out.println("Only players can execute this command.");

            }

            return false;
        }

        return false;
    }
}
