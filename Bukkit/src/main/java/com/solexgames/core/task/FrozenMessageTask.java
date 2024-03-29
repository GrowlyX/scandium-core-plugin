package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class FrozenMessageTask extends BukkitRunnable {

    public FrozenMessageTask() {
        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 5 * 20L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> CorePlugin.getInstance().getPlayerManager().getPlayer(player).isFrozen())
                .forEach(player -> CorePlugin.getInstance().getPlayerManager().sendFreezeMessage(player));
    }
}
