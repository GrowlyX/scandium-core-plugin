package com.solexgames.core.redis;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.redis.subscription.AbstractJedisSubscriber;
import com.solexgames.core.redis.subscription.extend.CoreJedisSubscriber;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GrowlyX
 * @since 3/1/2021
 * <p>
 * Holds instances to anything redis related.
 */

@Getter
@Setter
public class RedisManager {

    private JedisPool jedisPool;

    private RedisSettings settings;
    private RedisSubscriptions subscriptions;

    private CoreJedisSubscriber jedisSubscriber;

    private boolean active;

    /**
     * Initializes Redis Manager with values from {@link RedisSettings}.
     *
     * @param settings Instance to core redis settings.
     */
    public RedisManager(RedisSettings settings) {
        this.settings = settings;
        this.subscriptions = CorePlugin.getInstance().getSubscriptions();

        this.subscribe();
    }

    private void subscribe() {
        try {
            this.jedisPool = new JedisPool(this.settings.getHostAddress(), this.settings.getPort());
            Jedis jedis = this.jedisPool.getResource();

            if (this.settings.isAuth()) {
                jedis.auth(this.settings.getPassword());
            }

            this.jedisSubscriber = new CoreJedisSubscriber();
            (new Thread(() -> jedis.subscribe(this.jedisSubscriber, this.jedisSubscriber.getChannelName()))).start();

            jedis.connect();

            CorePlugin.getInstance().getLogger().info("[Redis] Connected to Redis backend.");
            this.active = true;
        } catch (Exception ignored) {
            CorePlugin.getInstance().getLogger().severe("[Redis] Could not connect to Redis backend.");
            this.active = false;
        }
    }

    public void unsubscribe() {
        try {
            this.jedisPool.destroy();
            this.jedisSubscriber.unsubscribe();
        } catch (Exception e) {
            System.out.println("[Redis] Could not destroy Redis Pool.");
        }
    }

    public void write(String json) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.auth(this.settings.getPassword());
            jedis.publish("Scandium:BUKKIT", json);
        }
    }

    public void write(String json, AbstractJedisSubscriber subscriber) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.auth(this.settings.getPassword());
            jedis.publish(subscriber.getChannelName(), json);
        }
    }
}
