package com.solexgames.core.redis;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.redis.sub.extend.CoreJedisSubscriber;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Getter
@Setter
public class RedisClient {

    private final String redisAddress;
    private final String redisPassword;
    private final int redisPort;

    private JedisPool jedisPool;
    private CoreJedisSubscriber coreJedisSubscriber;

    private boolean redisAuthentication;
    private boolean isClientActive;

    public RedisClient() {
        this.redisAddress = CorePlugin.getInstance().getDatabaseConfig().getString("redis.host");
        this.redisPassword = CorePlugin.getInstance().getDatabaseConfig().getString("redis.authentication.password");
        this.redisPort = CorePlugin.getInstance().getDatabaseConfig().getInt("redis.port");
        this.redisAuthentication = CorePlugin.getInstance().getDatabaseConfig().getBoolean("redis.authentication.enabled");

        this.subscribe();
    }

    private void subscribe() {
        try {
            this.jedisPool = new JedisPool(redisAddress, redisPort);
            Jedis jedis = this.jedisPool.getResource();

            if (redisAuthentication) jedis.auth(this.redisPassword);

            this.coreJedisSubscriber = new CoreJedisSubscriber(this);
            (new Thread(() -> jedis.subscribe(this.coreJedisSubscriber, "SCANDIUM"))).start();
            jedis.connect();
            this.setClientActive(true);

            CorePlugin.getInstance().getLogger().info("[Redis] Connected to Redis backend.");
        } catch (Exception ignored) {
            CorePlugin.getInstance().getLogger().severe("[Redis] Could not connect to Redis backend.");
            this.setClientActive(false);
        }
    }

    public void unsubscribe() {
        try {
            jedisPool.destroy();
            this.coreJedisSubscriber.unsubscribe();
        } catch (Exception e) {
            System.out.println("[Redis] Could not destroy Redis Pool.");
        }
    }

    public void write(String json) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.auth(this.redisPassword);
            jedis.publish("SCANDIUM", json);
        }
    }
}
