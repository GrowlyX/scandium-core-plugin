package com.solexgames.xenon.redis.exception;

public class InvalidSubscriptionException extends Exception {

    public InvalidSubscriptionException(String reason) {
        super(reason);
    }
}
