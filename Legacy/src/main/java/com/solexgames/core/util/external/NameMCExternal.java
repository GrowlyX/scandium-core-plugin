package com.solexgames.core.util.external;

import com.solexgames.core.CorePlugin;

import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NameMCExternal {

    public static boolean hasVoted(String uuid) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (Scanner scanner = new Scanner(new URL("https://api.namemc.com/server/" + CorePlugin.getInstance().getConfig().getString("settings.namemc-ip") + "/likes?profile=" + uuid).openStream()).useDelimiter("\\A")) {
                completableFuture.complete(Boolean.parseBoolean(scanner.next()));
            } catch (Exception ignored) {
                completableFuture.complete(false);
            }
        });

        AtomicBoolean result = new AtomicBoolean(false);
        completableFuture.thenAccept(result::set);

        return result.get();
    }
}
