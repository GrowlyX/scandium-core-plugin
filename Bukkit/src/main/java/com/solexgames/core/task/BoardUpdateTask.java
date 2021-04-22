package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class BoardUpdateTask extends BukkitRunnable {

    public BoardUpdateTask() {
        this.runTaskTimer(CorePlugin.getInstance(), 0L, 2L);
    }

    @Override
    public void run() {
        if (!ScoreBoard.getAllBoards().isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                final ScoreBoard scoreBoard = ScoreBoard.getAllBoards().get(player.getUniqueId());

                if (scoreBoard != null) {
                    scoreBoard.setTitle(scoreBoard.getTitle());
                    scoreBoard.setSlotsFromList(scoreBoard.getLines());
                }
            }
        }
    }
}
