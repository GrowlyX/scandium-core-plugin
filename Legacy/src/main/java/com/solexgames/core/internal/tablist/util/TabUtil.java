package com.solexgames.core.internal.tablist.util;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TabUtil {

    public static OfflinePlayer createNewFakePlayer(UUID uuid, String name) {
        return new OfflinePlayer() {
            public boolean isOnline() {
                return true;
            }

            public long getLastLogin() {
                return 0;
            }

            public long getLastLogout() {
                return 0;
            }

            public String getName() {
                return name;
            }

            public UUID getUniqueId() {
                return uuid;
            }

            public boolean isBanned() {
                return false;
            }

            public void setBanned(boolean b) {

            }

            public boolean isWhitelisted() {
                return false;
            }

            public void setWhitelisted(boolean b) {

            }

            public Player getPlayer() {
                return null;
            }

            public long getFirstPlayed() {
                return 0;
            }

            public long getLastPlayed() {
                return 0;
            }

            public boolean hasPlayedBefore() {
                return false;
            }

            public Location getBedSpawnLocation() {
                return null;
            }

            public Map<String, Object> serialize() {
                return null;
            }

            public boolean isOp() {
                return false;
            }

            public void setOp(boolean b) {

            }
        };
    }
}
