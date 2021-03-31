package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class PlayerSaveTask extends BukkitRunnable {

    public PlayerSaveTask() {
        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 30 * 20L);
    }

    @Override
    public void run() {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getPlayerManager().getAllProfiles().forEach((uuid, potPlayer) -> potPlayer.saveWithoutRemove()));
    }
}
