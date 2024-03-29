package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.impl.ReportMenu;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(label = "report", hidden = false)
public class ReportCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Color.translate(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>"));
        }

        if (args.length == 1) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                if (target == player) {
                    player.sendMessage(ChatColor.RED + "You cannot report yourself.");
                    return true;
                }

                new ReportMenu(player, target).open(player);
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        return false;
    }
}
