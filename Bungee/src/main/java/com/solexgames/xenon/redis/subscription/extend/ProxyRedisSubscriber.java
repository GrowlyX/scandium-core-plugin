package com.solexgames.xenon.redis.subscription.extend;

import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.redis.json.JsonAppender;
import com.solexgames.xenon.redis.subscription.AbstractJedisSubscriber;

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
