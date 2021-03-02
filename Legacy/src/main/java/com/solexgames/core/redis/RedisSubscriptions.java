package com.solexgames.core.redis;

import com.solexgames.core.redis.subscription.AbstractJedisSubscriber;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GrowlyX
 * @since 3/2/2021
 * <p>
 * Holds instances to all jedis subscriptions.
 */

@Getter
@NoArgsConstructor
public class RedisSubscriptions {

    private final List<AbstractJedisSubscriber> subscriptions = new ArrayList<>();

}
