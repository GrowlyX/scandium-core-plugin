package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.feed")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.setFoodLevel(20);

            player.sendMessage(Color.SECONDARY_COLOR + "Set your food level to " + Color.MAIN_COLOR + "20" + Color.SECONDARY_COLOR +".");
            player.sendMessage(Color.SECONDARY_COLOR + "You've reset your " + Color.MAIN_COLOR + "food level" + Color.SECONDARY_COLOR + ".");

            PlayerUtil.sendAlert(player, "reset food level");
        }

        if (args.length > 0) {
            final Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            } else {
                target.setFoodLevel(20);
                player.sendMessage(Color.SECONDARY_COLOR + "You've reset " + target.getDisplayName() + Color.SECONDARY_COLOR + "'s " + Color.MAIN_COLOR + "food level" + Color.SECONDARY_COLOR + ".");

                PlayerUtil.sendAlert(player, "reset food level for " + target.getName());
            }
        }
        return false;
    }
}