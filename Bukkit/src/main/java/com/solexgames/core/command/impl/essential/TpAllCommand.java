package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "tpall", permission = "scandium.command.tpall")
public class TpAllCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        Bukkit.getOnlinePlayers().forEach(player1 -> {
            PaperLib.teleportAsync(player1, player.getLocation())
                    .whenComplete((aBoolean, throwable) -> {
                       if (aBoolean) {
                           player1.sendMessage(ChatColor.GREEN + "You've been teleported to " + player.getDisplayName() + ChatColor.GREEN + ".");
                       } else {
                           player1.sendMessage(ChatColor.RED + "Sorry, we couldn't teleport you to " + player.getDisplayName() + ChatColor.RED + ".");
                       }
                    });
        });

        player.sendMessage(Color.SECONDARY_COLOR + "You've teleported all online players to you.");

        PlayerUtil.sendAlert(player, "teleported all players");

        return false;
    }
}
