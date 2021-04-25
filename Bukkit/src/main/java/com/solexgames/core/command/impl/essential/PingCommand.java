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

public class PingCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Color.SECONDARY_COLOR + "Your ping is currently: " + Color.MAIN_COLOR + PlayerUtil.getPing(player) + "ms" + Color.SECONDARY_COLOR + "!");
        }

        if (args.length == 1) {
            final Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                player.sendMessage(target.getDisplayName() + Color.SECONDARY_COLOR + "'s ping is currently: " + Color.MAIN_COLOR + PlayerUtil.getPing(target) + "ms" + Color.SECONDARY_COLOR + "!");
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }
        return false;
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }
}
