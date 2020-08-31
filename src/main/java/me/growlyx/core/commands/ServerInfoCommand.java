package me.growlyx.core.commands;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
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

                int players1 = Bukkit.getServer().getOnlinePlayers().size();
                int maxPlayers1 = Bukkit.getServer().getMaxPlayers();

                String players = Integer.toString(players1);
                String maxPlayers = Integer.toString(players1);

                for (String string: Core.instance.m.getConfig().getStringList("MESSAGES.SERVER-INFO")) {

                    string.replace("<name>", player.getDisplayName());
                    string.replace("<version>", Bukkit.getServer().getVersion());
                    string.replace("<players>", players);
                    string.replace("<max-players>", maxPlayers);

                    player.sendMessage(CC.translate(string));
                }

            } else {

                System.out.println("Only players can execute this command.");

            }

            return false;
        }

        return false;
    }
}
