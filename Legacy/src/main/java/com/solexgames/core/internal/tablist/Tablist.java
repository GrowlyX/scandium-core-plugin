package com.solexgames.core.internal.tablist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tablist {

    private final Scoreboard scoreboard;
    private final Player player;
    private final OutlastTab outlastTab;
    private final Map<Integer, String> entries = new HashMap<>();
    private final List<Object> fakePlayers = new ArrayList<>();
    private boolean enabled = true;

    public Tablist(Scoreboard scoreboard, Player player, OutlastTab outlastTab) {
        this.scoreboard = scoreboard;
        this.player = player;
        this.outlastTab = outlastTab;

        if (scoreboard.getTeam("z") == null)
            scoreboard.registerNewTeam("z");

        Team t = scoreboard.getTeam("z");

        int size = Bukkit.getOnlinePlayers().size();
        Player[] arr = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        Player online;
        for (int i = 0; i < size; i++) {
            online = arr[i];
            if (!t.getEntries().contains(online.getName()))
                t.addEntry(online.getName());

            if (online.getScoreboard().getTeam("z") == null)
                online.getScoreboard().registerNewTeam("z");

            Team team1 = online.getScoreboard().getTeam("z");

            if (!team1.getEntries().contains(player.getName()))
                team1.addEntry(player.getName());
        }

        size = this.outlastTab.getTablistVersion().getSlots(this.player) + 1;
        for (int i = 1; i < size; i++) {
            String key = getNextBlank();

            entries.put(i, key);
            if (getByPos(i) == null)
                scoreboard.registerNewTeam("a" + key);

            Team team = scoreboard.getTeam("a" + key);

            team.addEntry(entries.get(i));

            fakePlayers.add(outlastTab.getTablistVersion().createPlayer(player, key));
        }
        player.setScoreboard(scoreboard);
    }

    protected void update() {
        outlastTab.getTablistVersion().update(player);

        if (outlastTab.getTablist().getElements(player) == null || outlastTab.getTablist().getElements(player).isEmpty())
            return;

        int size = this.outlastTab.getTablistVersion().getSlots(this.player) + 1;

        for (int i = 1; i < size; i++) {
            if (TablistElement.getByPosition(player, i) != null) {
                TablistElement tablistElement = TablistElement.getByPosition(player, i);
                if (tablistElement.getDisplay().length() > 16) {
                    String prefix = splitStrings(tablistElement.getDisplay())[0];
                    String suffix = splitStrings(tablistElement.getDisplay())[1];

                    getByPos(i).setPrefix(prefix);
                    getByPos(i).setSuffix(suffix);
                } else {
                    getByPos(i).setPrefix(tablistElement.getDisplay() == null ? "" : tablistElement.getDisplay());
                    getByPos(i).setSuffix("");
                }
            } else {
                getByPos(i).setSuffix("");
                getByPos(i).setPrefix("");
            }
        }
    }

    public void enable() {
        if (enabled) return;
        int size = fakePlayers.size();
        for (int i = 0; i < size; i++) outlastTab.getTablistVersion().addPlayerInfo(player, fakePlayers.get(i));
        this.enabled = true;
    }

    public void disable() {
        if (!enabled) return;
        int size = fakePlayers.size();
        for (int i = 0; i < size; i++) outlastTab.getTablistVersion().removePlayerInfo(player, fakePlayers.get(i));
        //outlastTab.getTablistVersion().addAllOnlinePlayers(player);
        this.enabled = false;
    }

    public void destroy() {
        outlastTab.getTablists().remove(this);
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        for (String entry : scoreboard.getEntries()) scoreboard.resetScores(entry);
        for (Objective objective : scoreboard.getObjectives()) objective.unregister();
        for (Team team : scoreboard.getTeams()) team.unregister();
    }

    public Player getPlayer() {
        return player;
    }

    private Team getByPos(int slot) {
        return scoreboard.getTeam("a" + entries.get(slot));
    }

    private String repeat(String str, int repeat) {
        if (str == null)
            return null;

        if (repeat <= 0) {
            return "";
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= 8192) {
            return padding(repeat, str.charAt(0));
        }

        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                char ch = str.charAt(0);
                char[] output1 = new char[outputLength];
                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }
                return new String(output1);
            case 2:
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default:
                StringBuilder buf = new StringBuilder(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    private String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }

    private String[] splitStrings(String text) {
        final int lenght = text.length();
        if (lenght > 16) {
            String prefix = text.substring(0, 16);
            String suffix;

            if (prefix.charAt(15) == ChatColor.COLOR_CHAR || prefix.charAt(15) == '&') {
                prefix = prefix.substring(0, 15);
                suffix = text.substring(15, lenght);
            } else if (prefix.charAt(14) == ChatColor.COLOR_CHAR || prefix.charAt(14) == '&') {
                prefix = prefix.substring(0, 14);
                suffix = text.substring(14, lenght);
            } else suffix = ChatColor.getLastColors(prefix.replace('&', '§')) + text.substring(16, lenght);

            if (suffix.length() > 16)
                suffix = suffix.substring(0, 16);

            return new String[]{prefix, suffix};
        } else {
            return new String[]{text};
        }
    }

    private String getNextBlank() {
        for (String blank : getBlanks()) {
            if (scoreboard.getTeam(blank) != null) continue;
            if (entries.values().stream().filter(blank::equalsIgnoreCase).findFirst().orElse(null) != null) continue;

            return blank;
        }
        return null;
    }

    private List<String> getBlanks() {
        List<String> toReturn = new ArrayList<>();

        for (ChatColor color : ChatColor.values())
            for (int i = 0; i < 4; i++) toReturn.add(repeat(color + "", 4 - i) + ChatColor.RESET);

        return toReturn;
    }

    public List<Object> getFakePlayers() {
        return fakePlayers;
    }
}
