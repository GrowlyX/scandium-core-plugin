package com.solexgames.core.redis.experimental;

import com.solexgames.core.redis.annotation.Subscription;
import com.solexgames.core.redis.json.JsonAppender;
import com.solexgames.core.redis.packet.RedisAction;

public class JedisSubscriber {

    @Subscription(action = RedisAction.RANK_SETTINGS_UPDATE)
    public void onRankUpdate(JsonAppender data) {
        final String rank = data.getParam("rank");

        // lol
    }
}
