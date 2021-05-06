package com.solexgames.core.redis.experimental;

import com.solexgames.core.redis.annotation.Subscription;
import com.solexgames.core.redis.json.JsonAppender;
import com.solexgames.core.redis.packet.RedisAction;

import java.lang.reflect.Method;
import java.util.HashMap;

public class JedisListener implements JedisHandler {

    // ignore for now just testing
    // static
    public static HashMap<RedisAction, Method> actionMethodHashMap = new HashMap<>();

    public void test() {
        final Method[] methodList = this.getClass().getMethods();

        for (Method method : methodList) {
            if (method.isAnnotationPresent(Subscription.class)) {
                final Subscription subscription = method.getAnnotation(Subscription.class);

                JedisListener.actionMethodHashMap.put(subscription.action(), method);
            }
        }
    }

    @Subscription(action = RedisAction.RANK_SETTINGS_UPDATE)
    public void onRankUpdate(JsonAppender data) {
        final String rank = data.getParam("rank");

        // lol
    }
}
