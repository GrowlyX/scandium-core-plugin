package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "tpall")
public class TpAllCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.tpall")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        Bukkit.getOnlinePlayers().forEach(player1 -> player1.teleport(player.getLocation()));

        player.sendMessage(Color.SECONDARY_COLOR + "You've teleported all online players to you.");

        PlayerUtil.sendAlert(player, "teleported all players");

        return false;
    }
}
