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
import java.util.concurrent.atomic.AtomicInteger;

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
    private Objective sidebar;

    public ScoreBoard(Player player) {
        this.player = player;

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = this.scoreboard.registerNewObjective("sidebar", "dummy");
        this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(this.scoreboard);

        for (int i = 1; i <= 15; i++) {
            final Team team = this.scoreboard.registerNewTeam("SLOT_" + i);
            team.addEntry(genEntry(i));
        }

        ScoreBoard.getAllBoards().put(player.getUniqueId(), this);
    }

    public void setTitle() {
        String title = Color.translate((this.getTitle().length() > 32 ? this.getTitle().substring(0, 32) : this.getTitle()));

        if (!this.sidebar.getDisplayName().equals(title)) {
            this.sidebar.setDisplayName(title);
        }
    }

    private void setSlot(int slot, String text) {
        if (slot > 15) return;

        final Team team = this.scoreboard.getTeam("SLOT_" + slot);
        final String entry = this.genEntry(slot);

        if (!this.scoreboard.getEntries().contains(entry)) {
            this.sidebar.getScore(entry).setScore(slot);
        }

        String prefix = this.getFirstSplit(text);
        int lastIndex = prefix.lastIndexOf(167);

        final String lastColor = lastIndex >= 14 ? prefix.substring(lastIndex) : ChatColor.getLastColors(prefix);

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

    private void removeSlot(int slot) {
        final String entry = this.genEntry(slot);

        if (this.scoreboard.getEntries().contains(entry)) {
            this.scoreboard.resetScores(entry);
        }
    }

    public void setSlotsFromList() {
        AtomicInteger slot = new AtomicInteger(this.getLines().size());

        if (slot.get() < 15) {
            for (int i = (slot.get() + 1); i <= 15; i++) {
                this.removeSlot(i);
            }
        } else {
            for (String line : this.getLines()) {
                this.setSlot(slot.get(), line);
                slot.getAndDecrement();
            }
        }
    }

    public void updateForPlayer() {
        this.setSlotsFromList();
        this.setTitle();
    }

    private String genEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String s) {
        return s.length() > 16 ? s.substring(0, 16) : s;
    }

    private String getSecondSplit(String s) {
        if (s.length() > 32) {
            s = s.substring(0, 32);
        }

        return s.length() > 16 ? s.substring(16) : "";
    }

    public void remove() {
        this.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        ScoreBoard.getAllBoards().remove(this.getPlayer().getUniqueId());
    }

    /**
     * Gets the List of scoreboard lines
     *
     * @return All available lines from the implementation
     */
    public abstract List<String> getLines();

    /**
     * Gets the scoreboard title
     *
     * @return The scoreboard title
     */
    public abstract String getTitle();

}
