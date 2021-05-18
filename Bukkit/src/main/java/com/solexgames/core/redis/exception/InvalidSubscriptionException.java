package com.solexgames.core.redis.exception;

public class InvalidSubscriptionException extends Exception {

    public InvalidSubscriptionException(String reason) {
        super(reason);
    }
}
