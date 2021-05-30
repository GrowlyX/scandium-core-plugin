package com.solexgames.xenon.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author GrowlyX
 * @since 5/18/2021
 * <p>
 * Holds information for a jedis subscription method
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscription {

    String action();

}
