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
        this.sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
        this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

        final Objective tab = scoreboard.registerNewObjective("tab", "health");
        tab.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        player.setScoreboard(scoreboard);

        for (int i = 1; i <= 15; i++) {
            Team team = scoreboard.registerNewTeam("SLOT_" + i);
            team.addEntry(genEntry(i));
        }

        ScoreBoard.getAllBoards().put(player.getUniqueId(), this);
    }

    public void setupHearts() {
        final Objective name = scoreboard.registerNewObjective("name", "health");

        name.setDisplaySlot(DisplaySlot.BELOW_NAME);
        name.setDisplayName("§4❤");
    }

    public void setTitle(String title) {
        title = Color.translate(title);

        if (title.length() > 32) title = title.substring(0, 32);
        if (!sidebar.getDisplayName().equals(title)) sidebar.setDisplayName(title);
    }

    private void setSlot(int slot, String text) {
        if (slot > 15) return;

        Team team = scoreboard.getTeam("SLOT_" + slot);
        String entry = genEntry(slot);

        if (!scoreboard.getEntries().contains(entry)) sidebar.getScore(entry).setScore(slot);

        String prefix = getFirstSplit(text);

        int lastIndex = prefix.lastIndexOf(167);
        String lastColor = lastIndex >= 14 ? prefix.substring(lastIndex) : ChatColor.getLastColors(prefix);

        if (lastIndex >= 14) prefix = prefix.substring(0, lastIndex);

        String suffix = getFirstSplit(lastColor + getSecondSplit(text));

        if (!team.getPrefix().equals(prefix)) team.setPrefix(prefix);
        if (!team.getSuffix().equals(suffix)) team.setSuffix(suffix);
    }

    private void removeSlot(int slot) {
        String entry = genEntry(slot);
        if (scoreboard.getEntries().contains(entry)) scoreboard.resetScores(entry);
    }

    public void setSlotsFromList(List<String> list) {
        int slot = list.size();
        if (slot < 15) for (int i = (slot + 1); i <= 15; i++) removeSlot(i);

        for (String line : list) {
            setSlot(slot, line);
            slot--;
        }
    }

    private String genEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String s) {
        return s.length() > 16 ? s.substring(0, 16) : s;
    }

    private String getSecondSplit(String s) {
        if (s.length() > 32) s = s.substring(0, 32);
        return s.length() > 16 ? s.substring(16) : "";
    }

    public void remove() {
        this.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        ScoreBoard.getAllBoards().remove(this.getPlayer().getUniqueId());
    }

    public abstract List<String> getLines();
    public abstract String getTitle();

}
