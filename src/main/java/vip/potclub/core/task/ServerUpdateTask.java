package vip.potclub.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.util.RedisUtil;

public class ServerUpdateTask extends BukkitRunnable {

    public ServerUpdateTask() {
        this.runTaskTimer(CorePlugin.getInstance(), 0, CorePlugin.getInstance().getConfig().getInt("refresh-time") * 20L);
    }

    @Override
    public void run() {
        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onServerUpdate()));
    }
}
