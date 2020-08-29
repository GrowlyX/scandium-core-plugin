package me.growlyx.core.essentials.commands.gamemodes;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "gma", permission = "core.commands.gamemode"))
public class GMA implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                String adventure = "Adventure";

                player.setGameMode(GameMode.ADVENTURE);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.GAMEMODE").replace("<gamemode>", adventure)));

                return true;

            }

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}