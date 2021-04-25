package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlyCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.fly")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            if (player.isFlying()) {
                player.setAllowFlight(false);
                player.setFlying(false);

                player.sendMessage(Color.SECONDARY_COLOR + "You've " + ChatColor.RED + "disabled" + Color.SECONDARY_COLOR + " flight mode.");

                PlayerUtil.sendAlert(player, "disabled flight");
            } else {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.sendMessage(Color.SECONDARY_COLOR + "You've " + ChatColor.GREEN + "enabled" + Color.SECONDARY_COLOR + " flight mode.");

                PlayerUtil.sendAlert(player, "enabled flight");
            }
        }
        if (args.length == 1) {
            final Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                if (target.isFlying()) {
                    target.setFlying(false);
                    player.sendMessage(Color.SECONDARY_COLOR + "You've " + ChatColor.RED + "disabled" + Color.SECONDARY_COLOR + " flight mode for " + target.getDisplayName() + Color.SECONDARY_COLOR + ".");

                    PlayerUtil.sendAlert(player, "disabled flight for " + target.getName());
                } else {
                    target.setFlying(true);
                    player.sendMessage(Color.SECONDARY_COLOR + "You've " + ChatColor.GREEN + "enabled" + Color.SECONDARY_COLOR + " flight mode for " + target.getDisplayName() + Color.SECONDARY_COLOR + ".");

                    PlayerUtil.sendAlert(player, "enabled flight for " + target.getName());
                }
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }

        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("flight", "toggleflight");
    }
}
