package com.solexgames.core.uuid;

import com.solexgames.core.util.UUIDUtil;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class UUIDCache extends HashMap<String, UUID> {

    /**
     * Fetches a UUID from the cache
     * <p>
     *
     * @param playerName Player's name
     * @return The player's UUID from the cache, or if not from 's API, or else null.
     */
    public UUID getUuidFromUsername(String playerName) {
        if (playerName == null || playerName.equals("")) {
            return null;
        }

        final UUID uuid = this.getOrDefault(playerName, null);

        if (uuid == null) {
            return UUIDUtil.fetchUUID(playerName);
        } else {
            return this.getOrDefault(playerName, null);
        }
    }
}
