package com.solexgames.redis.subscription.extend;

import com.solexgames.CorePlugin;
import com.solexgames.redis.json.JsonAppender;
import com.solexgames.redis.subscription.AbstractJedisSubscriber;

public class ProxyRedisSubscriber extends AbstractJedisSubscriber {

    public ProxyRedisSubscriber() {
        super("SCANDIUM_PROXY");
    }

    @Override
    public void onMessage(String channel, String message) {
        CorePlugin.getInstance().getRedisExecutor().execute(() -> {
            JsonAppender jsonAppender = CorePlugin.GSON.fromJson(message, JsonAppender.class);

            switch (jsonAppender.getPacket()) {

            }
        });
    }
}
