package com.solexgames.core.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@UtilityClass
public class AsyncUtil {

    public static void run(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    public static void run(Runnable runnable, Executor executor) {
        CompletableFuture.runAsync(runnable, executor);
    }
}
