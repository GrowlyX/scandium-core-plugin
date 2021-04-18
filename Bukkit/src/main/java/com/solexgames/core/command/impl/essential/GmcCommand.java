package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GmcCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.gmc")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(Color.SECONDARY_COLOR + "You've set your gamemode to " + Color.MAIN_COLOR + "creative" + Color.SECONDARY_COLOR + ".");

            PlayerUtil.sendAlert(player, "gamemode creative");
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                target.setGameMode(GameMode.CREATIVE);
                player.sendMessage(Color.SECONDARY_COLOR + "You've set " + target.getDisplayName() + Color.SECONDARY_COLOR + "'s gamemode to " + Color.MAIN_COLOR + "creative" + Color.SECONDARY_COLOR + ".");

                PlayerUtil.sendAlert(player, "gamemode creative for " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }

        return false;
    }
}
