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

@Command(label = "gms", permission = "scandium.command.gms")
public class GmsCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        if (args.length == 0) {
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Color.SECONDARY_COLOR + "You've set your gamemode to " + Color.MAIN_COLOR + "survival" + Color.SECONDARY_COLOR + ".");

            PlayerUtil.sendAlert(player, "gamemode survival");
        }
        if (args.length == 1) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                target.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(Color.SECONDARY_COLOR + "You've set " + target.getDisplayName() + Color.SECONDARY_COLOR + "'s gamemode to " + Color.MAIN_COLOR + "survival" + Color.SECONDARY_COLOR + ".");

                PlayerUtil.sendAlert(player, "gamemode survival for " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        return false;
    }
}
