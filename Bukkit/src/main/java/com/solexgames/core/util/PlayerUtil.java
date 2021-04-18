package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

@UtilityClass
public final class PlayerUtil {

    private static Class<?> CRAFT_PLAYER;

    public static void sendAlert(Player player, String reason) {
        if (CorePlugin.getInstance().getServerSettings().isStaffAlertsEnabled()) {
            Bukkit.getOnlinePlayers().stream()
                    .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                    .filter(potPlayer -> potPlayer != null && potPlayer.isCanSeeStaffMessages() && potPlayer.getPlayer().hasPermission("scandium.staff"))
                    .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(CorePlugin.getInstance().getServerSettings().getAlertFormat()
                            .replace("<playername>", player.getName())
                            .replace("<message>", reason))
                    );
        }
    }

    /**
     * Gets a player's connection ping via reflection
     *
     * @param player specified player
     * @return the player's ping
     */
    public static int getPing(Player player) {
        try {
            final Object handle = PlayerUtil.CRAFT_PLAYER.getMethod("getHandle").invoke(player);

            return (Integer) handle.getClass().getDeclaredField("ping").get(handle);
        } catch (Exception ignored) {
            return 0;
        }
    }

    static {
        try {
            CRAFT_PLAYER = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".entity.CraftPlayer");
        } catch (Exception ignored) {
        }
    }
}
