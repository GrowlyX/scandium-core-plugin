package com.solexgames.core.redis.subscription;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.redis.RedisManager;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

/**
 * @author GrowlyX
 * @since March 2021
 */

@Getter
public abstract class AbstractJedisSubscriber extends JedisPubSub {

    private final String channelName;

    public AbstractJedisSubscriber(String channel) {
        this.channelName = channel;

        CorePlugin.getInstance().getSubscriptions().getSubscriptions().add(this);
        CorePlugin.getInstance().logConsole("&6[Redis] &eNow listening on jedis channel &6'" + channelName.toLowerCase() + "'&e.");
    }
}
