package com.solexgames.core.redis.sub;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.redis.RedisClient;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

@Getter
public abstract class AbstractJedisSubscriber extends JedisPubSub {

    private final RedisClient redisClient;

    public AbstractJedisSubscriber(String channel) {
        this.redisClient = CorePlugin.getInstance().getRedisClient();

        (new Thread(() -> this.redisClient.getJedisPool().getResource().subscribe(this, channel), (channel.toLowerCase() + "-jedis"))).start();

        CorePlugin.getInstance().getLogger().info("[Redis] Now listening on jedis channel '" + channel + "'.");
    }
}
