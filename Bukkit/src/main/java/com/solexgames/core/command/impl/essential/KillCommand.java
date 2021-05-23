package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "kill", permission = "scandium.command.kill")
public class KillCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            player.setHealth(0);
            player.sendMessage(Color.SECONDARY_COLOR + ("You've killed yourself."));

            PlayerUtil.sendAlert(player, "killed themself");
        }

        if (args.length > 0) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                target.setHealth(0);
                player.sendMessage(Color.SECONDARY_COLOR + "Killed " + target.getDisplayName() + Color.SECONDARY_COLOR + ".");

                PlayerUtil.sendAlert(player, "killed " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        return false;
    }
}
