package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import lombok.experimental.UtilityClass;

import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@UtilityClass
public final class VotingUtil {

    public static final String NAME_MC_URL = "https://api.namemc.com/server/$ip/likes?profile=$uuid";
    public static final String SERVER_IP = CorePlugin.getInstance().getConfig().getString("name-mc.address");

    /**
     * Fetches a player's Voting Status via NameMC's api
     *
     * @param uuidString The player's uuid string
     * @return a boolean
     */
    public static boolean hasVoted(String uuidString) {
        final AtomicBoolean result = new AtomicBoolean(false);

        CompletableFuture.supplyAsync(() -> {
            try (Scanner scanner = new Scanner(new URL(VotingUtil.NAME_MC_URL.replace("$ip", VotingUtil.SERVER_IP).replace("$uuid", uuidString) + uuidString).openStream()).useDelimiter("\\A")) {
                return Boolean.parseBoolean(scanner.next());
            } catch (Exception ignored) {
                return false;
            }
        }).thenAccept(result::set);

        return result.get();
    }
}
