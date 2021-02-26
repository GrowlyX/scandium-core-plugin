package com.solexgames.core.util.external;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;

public final class NameTagExternal {

    private static final String PREFIX = "nt_team_";

    public static void setupNameTag(Player player, Player other, ChatColor color) {
        Scoreboard scoreboard = player.getScoreboard();

        if (scoreboard.equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())) scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        Team team = player.getScoreboard().getTeam(getTeamName(color));
        if (team == null) {
            team = player.getScoreboard().registerNewTeam(getTeamName(color));
            team.setPrefix(color.toString());
        }

        if (!team.hasEntry(other.getName())) {
            resetNameTag(player, other);
            team.addEntry(other.getName());
        }

        player.setScoreboard(scoreboard);
    }

    public static void setupVanishTag(Player player, Player other) {
        Scoreboard scoreboard = player.getScoreboard();

        if (scoreboard.equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())) scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        Team team = player.getScoreboard().getTeam(getTeamName(ChatColor.MAGIC));
        if (team == null) {
            team = player.getScoreboard().registerNewTeam(getTeamName(ChatColor.MAGIC));
            team.setPrefix(Color.translate(CorePlugin.getInstance().getConfig().getString("settings.vanish-tag")));
        }

        if (!team.hasEntry(other.getName())) {
            resetNameTag(player, other);
            team.addEntry(other.getName());
        }

        player.setScoreboard(scoreboard);
    }

    public static void setupStaffModeTag(Player player, Player other) {
        Scoreboard scoreboard = player.getScoreboard();

        if (scoreboard.equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())) scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        Team team = player.getScoreboard().getTeam(getTeamName(ChatColor.STRIKETHROUGH));
        if (team == null) {
            team = player.getScoreboard().registerNewTeam(getTeamName(ChatColor.STRIKETHROUGH));
            team.setPrefix(Color.translate(CorePlugin.getInstance().getConfig().getString("settings.mod-mode-tag")));
        }

        if (!team.hasEntry(other.getName())) {
            resetNameTag(player, other);
            team.addEntry(other.getName());
        }

        player.setScoreboard(scoreboard);
    }

    public static void resetNameTag(Player player, Player other) {
        if (player != null && other != null && !player.equals(other)) {
            Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

            if (objective != null) objective.unregister();
            Arrays.asList(ChatColor.values()).forEach(chatColor -> {
                Team team = player.getScoreboard().getTeam(getTeamName(chatColor));
                if (team != null) team.removeEntry(other.getName());
            });
        }
    }

    private static String getTeamName(ChatColor color) {
        return PREFIX + color.ordinal();
    }
}
