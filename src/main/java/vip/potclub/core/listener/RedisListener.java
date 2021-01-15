package vip.potclub.core.listener;

import com.google.gson.Gson;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.JedisPubSub;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.redis.RedisMessage;

public class RedisListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                RedisMessage redisMessage = new Gson().fromJson(message, RedisMessage.class);
                switch (redisMessage.getPacket()) {
                    case CLAN_CHAT_UPDATE:
                        // TODO
                        break;
                    case CLAN_BROADCAST_UPDATE:
                        // TODO
                        break;
                    case CLAN_DATA_UPDATE:
                        // TODO
                        break;
                    default:
                        CorePlugin.getInstance().getLogger().info("[Redis] We received a message, but no params were registered.");
                        break;
                }
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }
}
