package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BoardUpdateTask extends BukkitRunnable {

    public BoardUpdateTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 0L, 2L);
    }

    @Override
    public void run() {
        if (ScoreBoard.getAllBoards().keySet().isEmpty()) return;

        Bukkit.getOnlinePlayers().forEach(player -> {
            ScoreBoard scoreBoard = ScoreBoard.getAllBoards().get(player.getUniqueId());
            if (scoreBoard != null) scoreBoard.update();
        });
    }
}
