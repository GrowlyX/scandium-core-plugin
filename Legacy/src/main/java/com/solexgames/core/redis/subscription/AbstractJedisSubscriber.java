package com.solexgames.core.redis.subscription;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.redis.RedisManager;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

@Getter
public abstract class AbstractJedisSubscriber extends JedisPubSub {

    private final String channelName;

    public AbstractJedisSubscriber(String channel) {
        this.channelName = channel;

        CorePlugin.getInstance().getSubscriptions().getSubscriptions().add(this);
        CorePlugin.getInstance().getLogger().info("[Redis] Now listening on jedis channel '" + channelName.toLowerCase() + "'.");
    }
}
