package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Getter
@NoArgsConstructor
public class NameTagManager {

    private final String prefix = "nt_team_";

    /**
     * Set up the nametag for the player
     *
     * @param player the player to send the tag to
     * @param target the target to set it up for
     * @param color  the color
     */
    public void setupNameTag(Player player, Player target, ChatColor color) {
        final Scoreboard scoreboard = this.getScoreboard(player);
        final Team team = this.getTeam(color, scoreboard);

        team.setPrefix(color.toString());

        if (!team.hasEntry(target.getName())) {
            this.resetNameTag(player, target);
            team.addEntry(target.getName());
        }

        player.setScoreboard(scoreboard);
    }

    /**
     * Get the scoreboard to use for the player
     *
     * @param player the player to change the scoreboard for
     * @return the scoreboard of the player
     */
    private Scoreboard getScoreboard(Player player) {
        final Scoreboard scoreboard = player.getScoreboard();

        return scoreboard.equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())
                ? Bukkit.getServer().getScoreboardManager().getNewScoreboard()
                : scoreboard;
    }

    /**
     * Get the team to change the prefix of for the player
     *
     * @param color      the color to get the team for
     * @param scoreboard the scoreboard to get the team from
     * @return the team
     */
    private Team getTeam(ChatColor color, Scoreboard scoreboard) {
        final Team team = scoreboard.getTeam(this.getTeamName(color));

        return team == null ? scoreboard.registerNewTeam(this.getTeamName(color)) : team;
    }

    public void setupVanishTag(Player player, Player target) {
        final Scoreboard scoreboard = this.getScoreboard(player);
        final Team team = this.getTeam(ChatColor.STRIKETHROUGH, scoreboard);

        team.setPrefix(ChatColor.STRIKETHROUGH.toString());

        if (!team.hasEntry(target.getName())) {
            this.resetNameTag(player, target);

            team.addEntry(target.getName());
            team.setPrefix(Color.translate(CorePlugin.getInstance().getConfig().getString("settings.vanish-tag")));
        }

        player.setScoreboard(scoreboard);
    }

    public void setupStaffModeTag(Player player, Player target) {
        final Scoreboard scoreboard = this.getScoreboard(player);
        final Team team = this.getTeam(ChatColor.STRIKETHROUGH, scoreboard);

        team.setPrefix(ChatColor.STRIKETHROUGH.toString());

        if (!team.hasEntry(target.getName())) {
            this.resetNameTag(player, target);

            team.addEntry(target.getName());
            team.setPrefix(Color.translate(CorePlugin.getInstance().getConfig().getString("settings.mod-mode-tag")));
        }

        player.setScoreboard(scoreboard);
    }

    /**
     * Reset the name tag for a player
     *
     * @param player the player to send it to
     * @param target the target to reset it for
     */
    public void resetNameTag(Player player, Player target) {
        if (player != null && target != null && !player.equals(target)) {
            final Scoreboard scoreboard = this.getScoreboard(player);
            final Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);

            if (objective != null) {
                objective.unregister();
            }

            for (ChatColor color : ChatColor.values()) {
                final Team team = scoreboard.getTeam(this.getTeamName(color));

                if (team != null) {
                    team.removeEntry(target.getName());
                }
            }
        }
    }

    /**
     * Get the team name per color
     *
     * @param color the color to get the team name from
     * @return the name of the team
     */
    private String getTeamName(ChatColor color) {
        return this.prefix + color.ordinal();
    }
}
