package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@UtilityClass
public final class LockedState {

    private static final String METADATA_KEY = "LOCKED";

    public static void lock(Player player, String message) {
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(CorePlugin.getInstance(), message));
    }

    public static void release(Player player) {
        player.removeMetadata(METADATA_KEY, CorePlugin.getInstance());
    }

    public static boolean isLocked(Player player) {
        return player.hasMetadata(METADATA_KEY);
    }

    public static String getMessage(Player player) {
        return player.getMetadata(METADATA_KEY).get(0).asString();
    }
}
