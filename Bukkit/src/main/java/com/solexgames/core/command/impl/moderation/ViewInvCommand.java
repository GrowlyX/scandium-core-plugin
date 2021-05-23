package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.menu.impl.player.ViewPlayerMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "viewinv", permission = "scandium.command.viewinv", aliases = {"inv", "inventory"})
public class ViewInvCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            new ViewPlayerMenu(player).openMenu(player);
        }

        if (args.length == 1) {
            if (!player.hasPermission("scandium.command.viewinv.other")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }

            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                new ViewPlayerMenu(target).openMenu(player);
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        return false;
    }
}
