package com.solexgames.core.redis;

import com.solexgames.core.redis.exception.InvalidHandlerException;
import com.solexgames.core.redis.handler.JedisHandler;
import com.solexgames.core.redis.json.JsonAppender;
import com.solexgames.core.redis.packet.RedisAction;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Getter
@Setter
public class JedisManager {

    private final HashMap<RedisAction, Method> jedisActionHandlers = new HashMap<>();

    private final String channel;
    private final RedisSettings settings;

    private JedisPool jedisPool;
    private JedisHandler jedisHandler;
    private JedisPubSub jedisPubSub;

    @SneakyThrows
    public JedisManager(String channel, RedisSettings settings, JedisHandler jedisHandler) {
        this.settings = settings;
        this.channel = channel;
        this.jedisHandler = jedisHandler;

        this.jedisPool = new JedisPool(this.settings.getHostAddress(), this.settings.getPort());

        this.registerJedisHandlerMethods();
        this.connect();
    }

    private void connect() {
        this.jedisPubSub = new JedisSubscription(this);

        CompletableFuture.runAsync(() -> {
            try (final Jedis jedis = this.jedisPool.getResource()) {
                this.authenticate(jedis);

                try {
                    jedis.subscribe(this.jedisPubSub, this.channel);
                } finally {
                    jedis.connect();
                }

                Logger.getGlobal().info("Now reading on jedis channel \"" + this.channel + "\"");
            } catch (Exception exception) {
                exception.printStackTrace();

                Logger.getGlobal().severe("Something went wrong while trying to subscribe to \"" + this.channel + "\".");
            }
        });
    }

    public void authenticate(Jedis jedis) {
        if (this.settings.isAuth()) {
            jedis.auth(this.settings.getPassword());
        }
    }

    public void disconnect() {
        try {
            if (this.jedisPool != null) {
                this.jedisPool.close();
            }

            if (this.jedisPubSub != null) {
                this.jedisPubSub.unsubscribe();
            }

            Logger.getGlobal().info("No longer reading on jedis channel " + this.channel);
        } catch (Exception ignored) {
            Logger.getGlobal().info("Something went wrong while trying to disconnect from jedis.");
        }
    }

    public void publish(String json) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            this.authenticate(jedis);
            jedis.publish(this.channel, json);
        }
    }

    public void registerJedisHandlerMethods() throws InvalidHandlerException {
        final Method[] methodList = this.jedisHandler.getClass().getMethods();

        for (Method method : methodList) {
            if (method.isAnnotationPresent(com.solexgames.core.redis.annotation.JedisSubscription.class)) {
                final com.solexgames.core.redis.annotation.JedisSubscription subscription = method.getAnnotation(com.solexgames.core.redis.annotation.JedisSubscription.class);

                if (method.getParameterTypes().length > 1) {
                    throw new InvalidHandlerException("Handler has more than 1 parameter");
                }

                if (method.getParameterTypes()[0] != JsonAppender.class) {
                    throw new InvalidHandlerException("Handler parameter is not JsonAppender");
                }

                this.jedisActionHandlers.put(subscription.action(), method);
            }
        }
    }
}
