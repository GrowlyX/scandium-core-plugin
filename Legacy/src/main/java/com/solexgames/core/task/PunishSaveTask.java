package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.punishment.Punishment;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class PunishSaveTask extends BukkitRunnable {

    public PunishSaveTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 30 * 20L);
    }

    @Override
    public void run() {
        Punishment.getAllPunishments().forEach(Punishment::savePunishment);
    }
}
