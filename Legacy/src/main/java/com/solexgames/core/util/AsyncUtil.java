package com.solexgames.core.util;

import com.solexgames.core.util.callback.AsyncCallback;
import lombok.experimental.UtilityClass;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

@UtilityClass
public class AsyncUtil {

    public static void run(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    public static void run(Runnable runnable, Executor executor) {
        CompletableFuture.runAsync(runnable, executor);
    }

    public static <T> T processAndGetAsync(AsyncCallback asyncCallback) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        AtomicReference<T> reference = new AtomicReference<>();

        CompletableFuture.runAsync(() -> completableFuture.complete(asyncCallback.callback()));
        completableFuture.thenAccept(reference::set);

        return reference.get();
    }
}
