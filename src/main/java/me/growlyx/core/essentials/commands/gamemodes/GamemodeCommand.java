package me.growlyx.core.essentials.commands.gamemodes;

import me.growlyx.core.essentials.Essentials;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "gamemode", permission = "core.commands.gamemode", aliases = {"gm", "g"}))
public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                player.sendMessage(CC.translate("&cUsage: /gamemode <gamemode>"));

                return true;

            } else if (args[0].equals("creative")) {

                player.setGameMode(GameMode.CREATIVE);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.GAMEMODE") + "Creative."));

                return true;

            } else if (args[0].equals("survival")) {

                String survival = "Survival";

                player.setGameMode(GameMode.SURVIVAL);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.GAMEMODE").replace("<gamemode>", survival)));

                return true;

            } else if (args[0].equals("adventure")) {

                String adventure = "Adventure";

                player.setGameMode(GameMode.ADVENTURE);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.GAMEMODE").replace("<gamemode>", adventure)));

                return true;

            } else if (args[0].equals("spec")) {

                player.setGameMode(GameMode.SPECTATOR);

                String spectator = "Spectator";

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.GAMEMODE").replace("<gamemode>", spectator)));

                return true;

            } else if (args[0].equals("c")) {

                player.setGameMode(GameMode.CREATIVE);

                String creative = "Creative";

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.GAMEMODE").replace("<gamemode>", creative)));

                return true;

            } else if (args[0].equals("s")) {

                String survival = "Survival";

                player.setGameMode(GameMode.SURVIVAL);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.GAMEMODE").replace("<gamemode>", survival)));

                return true;

            } else if (args[0].equals("a")) {

                String adventure = "Adventure";

                player.setGameMode(GameMode.ADVENTURE);

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.GAMEMODE").replace("<gamemode>", adventure)));

                return true;

            } else if (args[0].equals("spectator")) {

                player.setGameMode(GameMode.SPECTATOR);

                String spectator = "Spectator";

                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.GAMEMODE").replace("<gamemode>", spectator)));

                return true;

            }

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}