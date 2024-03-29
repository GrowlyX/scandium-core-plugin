package com.solexgames.xenon.redis;

import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.redis.json.JsonAppender;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * @author GrowlyX
 * @since 5/18/2021
 * <p>
 * Handles all incoming packets from the jedis subscription
 * defined in the {@link JedisManager} instance.
 */

@RequiredArgsConstructor
public class JedisSubscription extends JedisPubSub {

    private final JedisManager jedisManager;

    @Override
    public void onMessage(String channel, String message) {
        CompletableFuture.runAsync(() -> {
            final JsonAppender jsonAppender = CorePlugin.GSON.fromJson(message, JsonAppender.class);
            final String redisAction = jsonAppender.getPacket();
            final Method method = this.jedisManager.getJedisActionHandlers().get(redisAction);

            if (method != null) {
                try {
                    method.invoke(this.jedisManager.getJedisHandler(), jsonAppender);
                } catch (Exception exception) {
                    exception.printStackTrace();

                    Logger.getGlobal().severe("Couldn't handle this packet: " + redisAction);
                }
            }

            //                Logger.getGlobal().severe("Couldn't handle this packet as a handler does not exist: " + redisAction);
        });
    }
}
