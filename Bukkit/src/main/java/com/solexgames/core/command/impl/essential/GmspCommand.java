package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(label = "gmsp")
public class GmspCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.gmsp")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (Bukkit.getServer().getVersion().contains("1.7")) {
            return false;
        }

        if (args.length == 0) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Color.SECONDARY_COLOR + "You've set your gamemode to " + Color.MAIN_COLOR + "spectator" + Color.SECONDARY_COLOR + ".");

            PlayerUtil.sendAlert(player, "gamemode spectator");
        }
        if (args.length == 1) {
            final Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                target.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(Color.SECONDARY_COLOR + "You've set " + target.getDisplayName() + Color.SECONDARY_COLOR + "'s gamemode to " + Color.MAIN_COLOR + "spectator" + Color.SECONDARY_COLOR + ".");

                PlayerUtil.sendAlert(player, "gamemode spectator for " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }

        return false;
    }
}
