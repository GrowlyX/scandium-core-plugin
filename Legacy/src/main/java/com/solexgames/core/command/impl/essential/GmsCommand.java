package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GmsCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.gms")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Color.SECONDARY_COLOR + "You've set your gamemode to " + Color.MAIN_COLOR + "survival" + Color.SECONDARY_COLOR + ".");

            StaffUtil.sendAlert(player, "gamemode survival");
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                target.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(Color.SECONDARY_COLOR + "You've set " + target.getDisplayName() + Color.SECONDARY_COLOR + "'s gamemode to " + Color.MAIN_COLOR + "survival" + Color.SECONDARY_COLOR + ".");

                StaffUtil.sendAlert(player, "gamemode survival for " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }

        return false;
    }
}
