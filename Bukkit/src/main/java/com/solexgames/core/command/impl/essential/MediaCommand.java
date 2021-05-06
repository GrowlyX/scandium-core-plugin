package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.menu.impl.media.MediaViewMenu;
import com.solexgames.core.menu.impl.media.MediaManagerMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "media", hidden = false)
public class MediaCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            new MediaManagerMenu(player).open(player);
        }
        if (args.length == 1) {
            final Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                new MediaViewMenu(target).open(player);
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }

        return false;
    }
}
