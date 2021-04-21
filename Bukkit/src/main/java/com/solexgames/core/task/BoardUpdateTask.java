package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

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
        Bukkit.getOnlinePlayers().stream()
                .map(player -> ScoreBoard.getAllBoards().get(player.getUniqueId()))
                .filter(Objects::nonNull)
                .forEach(ScoreBoard::updateForPlayer);
    }
}
