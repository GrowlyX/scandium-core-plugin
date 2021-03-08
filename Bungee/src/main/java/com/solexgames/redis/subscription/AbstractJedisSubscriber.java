package com.solexgames.redis.subscription;

import com.solexgames.CorePlugin;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

@Getter
public abstract class AbstractJedisSubscriber extends JedisPubSub {

    private final String channelName;

    public AbstractJedisSubscriber(String channel) {
        this.channelName = channel;

        CorePlugin.getInstance().getLogger().info("[Redis] Now listening on jedis channel '" + channelName.toLowerCase() + "'.");
    }
}
