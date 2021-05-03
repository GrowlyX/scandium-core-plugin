package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GrowlyX
 * @since 4/15/2021
 */

public class ServerLoadingTask extends BukkitRunnable {

    private final AtomicInteger atomicInteger;

    public ServerLoadingTask() {
        this.atomicInteger = new AtomicInteger(5);

        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        switch (this.atomicInteger.decrementAndGet()) {
            case 5: case 4: case 3: case 2:
            case 1:
                CorePlugin.getInstance().logConsole(Color.SECONDARY_COLOR + "The server will be available in " + Color.MAIN_COLOR + this.atomicInteger.get() + Color.SECONDARY_COLOR + " seconds.");
                break;
            case 0:
                RedisUtil.publishAsync(RedisUtil.onServerOnline());

                CorePlugin.getInstance().getServerSettings().setCanJoin(true);
                CorePlugin.getInstance().logConsole(Color.MAIN_COLOR + "The server's now available!");
                break;
        }
    }
}
