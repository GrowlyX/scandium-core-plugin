package com.solexgames.core.board.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ModModeBoard extends ScoreBoard {

    public final ServerType network;
    public final PotPlayer potPlayer;

    public ModModeBoard(Player player) {
        super(player);

        this.potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.getPlayer());
        this.network = CorePlugin.getInstance().getServerManager().getNetwork();
    }

    @Override
    public List<String> getLines() {
        final List<String> lines = new ArrayList<>();

        lines.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");
        lines.add((!potPlayer.isVanished() ? ChatColor.GREEN + "Visible (Showing staff)" : ChatColor.RED + "Hidden (Showing staff)"));
        lines.add(network.getSecondaryColor() + "Channel: " + network.getMainColor() + (potPlayer.getChannel() != null ? Color.translate(getChannel(potPlayer.getChannel())) : "Regular"));
        lines.add(network.getSecondaryColor() + "Players: " + network.getMainColor() + Bukkit.getOnlinePlayers().size());
        lines.add(network.getSecondaryColor() + "TPS: " + RedisUtil.getTicksPerSecondFormatted());
        lines.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------");

        return lines;
    }

    @Override
    public String getTitle() {
        return network.getSecondaryColor() + ChatColor.BOLD.toString() + "Mod Mode";
    }

    public String getChannel(ChatChannelType chatChannelType) {
        switch (chatChannelType) {
            case DEV:
                return "&3Developer";
            case STAFF:
                return "&bStaff";
            case HOST:
                return "&2Host";
            case MANAGER:
                return "&4Manager";
            case ADMIN:
                return "&cAdmin";
            default:
                return "Normal";
        }
    }
}
