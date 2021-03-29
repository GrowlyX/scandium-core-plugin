package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class ServerUpdateTask extends BukkitRunnable {

    public ServerUpdateTask() {
        this.runTaskTimer(CorePlugin.getInstance(), 0, CorePlugin.getInstance().getConfig().getInt("refresh-time") * 20L);
    }

    @Override
    public void run() {
        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisManager().write(RedisUtil.onServerUpdate()));
    }
}
