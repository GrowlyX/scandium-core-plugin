package com.solexgames.core.redis.annotation;

import com.solexgames.core.redis.packet.RedisAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscription {

    RedisAction action();

}
