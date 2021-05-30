package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.util.Color;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 5/30/2021
 */

@Command(label = "whatserveramion", aliases = {"whatamion", "myserver"}, permission = "scandium.staff")
public class WhatServerAmIOnCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final NetworkPlayer networkPlayer = CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(player);

        if (networkPlayer == null) {
            player.sendMessage(ChatColor.GRAY + "[SG] " + ChatColor.RED + "Something went terribly wrong while trying to execute this command.");
            return false;
        }

        player.sendMessage(Color.SECONDARY_COLOR + "You've been connected to " + Color.MAIN_COLOR + CorePlugin.getInstance().getServerName() + Color.SECONDARY_COLOR + " for " + Color.MAIN_COLOR + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - networkPlayer.getConnectionTime(), true, true) + Color.SECONDARY_COLOR + ".");

        return false;
    }
}
