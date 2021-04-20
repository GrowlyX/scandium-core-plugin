package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class PlayerDataUpdateTask extends BukkitRunnable {

    public PlayerDataUpdateTask() {
        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 120 * 20L);
    }

    @Override
    public void run() {
        CorePlugin.getInstance().getPlayerManager().getAllProfiles().forEach((uuid, potPlayer) -> {
            potPlayer.saveWithoutRemove();
        });
    }
}
