package com.solexgames.core.board;

import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 4/15/2021
 */

@Getter
@Setter
public abstract class ScoreBoard {

    @Getter
    private static HashMap<UUID, ScoreBoard> allBoards = new HashMap<>();

    private Player player;
    private Scoreboard scoreboard;
    private Objective objective;

    public ScoreBoard(Player player) {
        this.player = player;

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective("sidebar", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.player.setScoreboard(this.scoreboard);

        for (int i = 1; i <= 15; i++) {
            final Team team = this.scoreboard.registerNewTeam("SLOT_" + i);
            team.addEntry(this.getNewEntry(i));
        }

        ScoreBoard.getAllBoards().put(player.getUniqueId(), this);
    }

    public ScoreBoard enableBelowNameTagHearts() {
        final Objective objective = this.scoreboard.registerNewObjective("name", "health");

        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName("§4❤");

        return this;
    }

    public ScoreBoard enableTabListHearts() {
        final Objective tab = this.scoreboard.registerNewObjective("tab", "health");
        tab.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        return this;
    }

    public void setTitle(String title) {
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        this.objective.setDisplayName(Color.translate(title));
    }

    private void setSlot(int slot, String text) {
        if (slot < 15) {
            final Team team = this.scoreboard.getTeam("SLOT_" + slot);
            final String entry = this.getNewEntry(slot);

            if (!this.scoreboard.getEntries().contains(entry)) {
                this.objective.getScore(entry).setScore(slot);
            }

            String prefix = this.getFirstSplit(text);
            int lastIndex = prefix.lastIndexOf(167);

            String lastColor = lastIndex >= 14 ? prefix.substring(lastIndex) : ChatColor.getLastColors(prefix);

            if (lastIndex >= 14) {
                prefix = prefix.substring(0, lastIndex);
            }

            String suffix = this.getFirstSplit(lastColor + this.getSecondSplit(text));

            if (!team.getPrefix().equals(prefix)) {
                team.setPrefix(prefix);
            }
            if (!team.getSuffix().equals(suffix)) {
                team.setSuffix(suffix);
            }
        }
    }

    private void removeSlot(int slot) {
        final String entry = this.getNewEntry(slot);

        if (scoreboard.getEntries().contains(entry)) {
            this.scoreboard.resetScores(entry);
        }
    }

    public void setSlotsFromList(List<String> list) {
        int slot = list.size();

        if (slot < 15) {
            for (int i = (slot + 1); i <= 15; i++) {
                this.removeSlot(i);
            }
        }

        for (String line : list) {
            this.setSlot(slot, line);
            slot--;
        }
    }

    private String getNewEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String string) {
        return string.length() > 16 ? string.substring(0, 16) : string;
    }

    private String getSecondSplit(String string) {
        if (string.length() > 32) {
            string = string.substring(0, 32);
        }

        return string.length() > 16 ? string.substring(16) : "";
    }

    public void remove() {
        this.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        ScoreBoard.getAllBoards().remove(this.getPlayer().getUniqueId());
    }

    public abstract List<String> getLines();
    public abstract String getTitle();

    public void update() {
        this.setTitle(this.getTitle());
        this.setSlotsFromList(this.getLines());
    }
}
