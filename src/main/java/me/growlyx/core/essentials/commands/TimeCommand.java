package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "time", permission = "core.commands.gamemode", aliases = {"timeset", "time set"}))
public class TimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                player.sendMessage(CC.translate("&cUsage: /time <day, night, midnight, sunset>"));

                return true;

            } else if (args[0].equals("day")) {

                player.setPlayerTime(6000L, false);
                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&7It is now &6Day&7."));

                return true;

            } else if (args[0].equals("night")) {

                player.setPlayerTime(18000L, false);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&7It is now &6Night&7."));

                return true;

            } else if (args[0].equals("sunset")) {

                player.setPlayerTime(12000, false);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&7It is now &6Sunset&7."));

                return true;

            } else if (args[0].equals("midnight")) {

                player.setPlayerTime(20000L, false);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&7It is now &6Midnight&7."));

                return true;

            }

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}