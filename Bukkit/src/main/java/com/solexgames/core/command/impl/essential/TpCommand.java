package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "tp", aliases = "teleport")
public class TpCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!sender.hasPermission("scandium.command.tp")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.translate(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>."));
        }

        if (args.length == 1) {
            final Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                player.teleport(target.getLocation());

                player.sendMessage(Color.SECONDARY_COLOR + "You've been teleported to " + target.getDisplayName() + Color.SECONDARY_COLOR + ".");

                PlayerUtil.sendAlert(player, "teleported to " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }
        return false;
    }
}
