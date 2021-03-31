package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class ServerUpdateTask extends BukkitRunnable {

    public ServerUpdateTask() {
        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 10L * 20L);
    }

    @Override
    public void run() {
        RedisUtil.writeAsync(RedisUtil.onServerUpdate());
    }
}
