package com.solexgames.xenon.redis;

import com.solexgames.xenon.redis.annotation.Subscription;
import com.solexgames.xenon.redis.exception.InvalidSubscriptionException;
import com.solexgames.xenon.redis.handler.JedisHandler;
import com.solexgames.xenon.redis.json.JsonAppender;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.Callback;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * @author GrowlyX
 * @since 5/18/2021
 * <p>
 * Management for the jedis system
 */

@Getter
@Setter
public class JedisManager {

    private final HashMap<String, Method> jedisActionHandlers = new HashMap<>();

    private final String channel;
    private final JedisSettings settings;

    private JedisPool jedisPool;
    private JedisHandler jedisHandler;
    private JedisPubSub jedisPubSub;

    public JedisManager(String channel, JedisSettings settings, JedisHandler jedisHandler) throws InvalidSubscriptionException {
        this.settings = settings;
        this.channel = channel;
        this.jedisHandler = jedisHandler;

        this.jedisPool = new JedisPool(this.settings.getHostAddress(), this.settings.getPort());

        if (this.jedisHandler != null) {
            this.registerSubscriptions();
            this.connect();
        }
    }

    private void connect() {
        this.jedisPubSub = new JedisSubscription(this);

        CompletableFuture.runAsync(() -> {
            try (final Jedis jedis = this.jedisPool.getResource()) {
                if (this.jedisPubSub != null) {
                    this.authenticate(jedis);

                    try {
                        jedis.subscribe(this.jedisPubSub, this.channel);
                    } finally {
                        jedis.connect();
                    }

                    Logger.getGlobal().info("Now reading on jedis channel \"" + this.channel + "\"");
                }
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

    public void get(Callback<Jedis> jedisCallback) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            this.authenticate(jedis);

            jedisCallback.done(jedis, null);
        }
    }

    public void registerSubscriptions() throws InvalidSubscriptionException {
        final Method[] methodList = this.jedisHandler.getClass().getMethods();

        for (Method method : methodList) {
            if (method.isAnnotationPresent(Subscription.class)) {
                final Subscription subscription = method.getAnnotation(Subscription.class);

                if (method.getParameterTypes().length > 1) {
                    throw new InvalidSubscriptionException("Handler has more than 1 parameter");
                }

                if (method.getParameterTypes()[0] != JsonAppender.class) {
                    throw new InvalidSubscriptionException("Handler parameter is not JsonAppender");
                }

                if (!method.getName().startsWith("on")) {
                    throw new InvalidSubscriptionException("Handler method does not match with naming conventions (on<incomingPacket>)");
                }

                this.jedisActionHandlers.put(subscription.action(), method);
            }
        }
    }
}
