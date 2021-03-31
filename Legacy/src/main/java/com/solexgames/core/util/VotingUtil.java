package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;

import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public final class VotingUtil {

    public static final String NAME_MC_URL = "https://api.namemc.com/server/$ip/likes?profile=$uuid";
    public static final String SERVER_IP = CorePlugin.getInstance().getConfig().getString("settings.namemc-ip");

    public static boolean hasVoted(String uuidString) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (Scanner scanner = new Scanner(new URL(VotingUtil.NAME_MC_URL.replace("$ip", VotingUtil.SERVER_IP).replace("$uuid", uuidString) + uuidString).openStream()).useDelimiter("\\A")) {
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
