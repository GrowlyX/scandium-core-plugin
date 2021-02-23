package com.solexgames.core.board.extend;

import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.util.Color;
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

        this.setTitle(Color.translate("&b&lPotClub &7" + '⎜' + " &fBuild"));

        this.setSlotsFromList(lines);
    }
}
