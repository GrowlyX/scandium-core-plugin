package com.solexgames.core.redis.sub;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.redis.RedisClient;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

@Getter
public abstract class AbstractJedisSubscriber extends JedisPubSub {

    private final RedisClient redisClient;
    private final String channelName;

    public AbstractJedisSubscriber(String channel, RedisClient redisClient) {
        this.redisClient = redisClient;
        this.channelName = channel;



        CorePlugin.getInstance().getLogger().info("[Redis] Now listening on jedis channel '" + channelName + "'.");
    }
}
