package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerSaveTask extends BukkitRunnable {

    public PlayerSaveTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 36000L, 36000L);
    }

    @Override
    public void run() {
        PotPlayer.getProfilePlayers().forEach((uuid, potPlayer) -> potPlayer.saveWithoutRemove());
    }
}
