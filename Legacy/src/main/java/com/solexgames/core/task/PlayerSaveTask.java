package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerSaveTask extends BukkitRunnable {

    public PlayerSaveTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 10 * 20L);
    }

    @Override
    public void run() {
        CorePlugin.getInstance().getPlayerManager().getAllProfiles().forEach((uuid, potPlayer) ->
                potPlayer.saveWithoutRemove()
        );
    }
}
