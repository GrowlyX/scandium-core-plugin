package com.solexgames.core.board.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ModSuiteBoard extends ScoreBoard {

    public ModSuiteBoard(Player player) {
        super(player);
    }

    @Override
    public void update() {
        List<String> lines = new ArrayList<>();
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.getPlayer());

        this.setTitle(network.getMainColor() + ChatColor.BOLD.toString() + "Staff");

        lines.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "------------------------");
        lines.add(network.getMainColor() + ChatColor.BOLD.toString() + "Player:");
        lines.add(ChatColor.GRAY + " * " + network.getSecondaryColor() + "Vanish: " + (potPlayer.isVanished() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        lines.add("  ");
        lines.add(network.getMainColor() + ChatColor.BOLD.toString() + "Server:");
        lines.add(ChatColor.GRAY + " * " + network.getSecondaryColor() + "TPS: " + network.getMainColor() + RedisUtil.getTicksPerSecondFormatted());
        lines.add(ChatColor.GRAY + " * " + network.getSecondaryColor() + "Whitelist: " + (CorePlugin.getInstance().getWhitelistConfig().getBoolean("whitelist") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        lines.add(ChatColor.GRAY + " * " + network.getSecondaryColor() + "Players: " + network.getMainColor() + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        lines.add(ChatColor.GRAY + " * " + network.getSecondaryColor() + "Staff: " + network.getMainColor() + Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("scandium.staff")).count());
        lines.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "------------------------");

        this.setSlotsFromList(lines);
    }
}
