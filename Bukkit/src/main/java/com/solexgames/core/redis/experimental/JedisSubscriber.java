package com.solexgames.core.redis.experimental;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.redis.json.JsonAppender;
import com.solexgames.core.redis.packet.RedisAction;
import lombok.SneakyThrows;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Method;

public class JedisSubscriber extends JedisPubSub {

    @Override
    @SneakyThrows
    public void onMessage(String channel, String message) {
        final JsonAppender jsonAppender = CorePlugin.GSON.fromJson(message, JsonAppender.class);
        final RedisAction redisAction = jsonAppender.getPacket();

        // i know static just testing not final
        final Method method = JedisListener.actionMethodHashMap.get(redisAction);

        if (method != null) {
            method.invoke(this, jsonAppender);
        } else {
            System.out.println("Couldn't handle this packet");
        }
    }
}
