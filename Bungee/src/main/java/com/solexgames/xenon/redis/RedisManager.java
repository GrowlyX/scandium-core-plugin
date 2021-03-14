package com.solexgames.xenon.redis;

import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.redis.subscription.AbstractJedisSubscriber;
import com.solexgames.xenon.redis.subscription.extend.ProxyRedisSubscriber;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
    private AbstractJedisSubscriber jedisSubscriber;

    private boolean active;

    /**
     * Initializes Redis Manager with values from {@link RedisSettings}.
     *
     * @param settings Instance to core redis settings.
     */
    public RedisManager(RedisSettings settings) {
        this.settings = settings;

        this.subscribe();
    }

    private void subscribe() {
        try {
            this.jedisPool = new JedisPool(settings.getHostAddress(), settings.getPort());
            Jedis jedis = this.jedisPool.getResource();

            if (settings.isAuth()) {
                jedis.auth(settings.getPassword());
            }

            this.jedisSubscriber = new ProxyRedisSubscriber();
            (new Thread(() -> jedis.subscribe(jedisSubscriber, jedisSubscriber.getChannelName()))).start();

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
            jedisPool.destroy();
            this.jedisSubscriber.unsubscribe();
        } catch (Exception e) {
            System.out.println("[Redis] Could not destroy Redis Pool.");
        }
    }

    public void write(String json) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.auth(this.settings.getPassword());
            jedis.publish("Scandium:PROXY", json);
        }
    }

    public void write(String json, AbstractJedisSubscriber subscriber) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.auth(this.settings.getPassword());
            jedis.publish(subscriber.getChannelName(), json);
        }
    }
}
