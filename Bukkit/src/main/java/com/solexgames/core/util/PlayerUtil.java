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

    public static void sendAlert(Player player, String reason) {
        if (CorePlugin.getInstance().getServerSettings().isStaffAlertsEnabled()) {
            Bukkit.getOnlinePlayers().stream()
                    .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                    .filter(potPlayer -> potPlayer != null && potPlayer.isCanSeeStaffMessages() && potPlayer.getPlayer().hasPermission(CorePlugin.getInstance().getConfig().getString("settings.staff-command-alerts-permission")))
                    .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getConfig().getString("settings.staff-command-alerts-format").replace("<playername>", player.getName()).replace("<message>", reason))));
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
            final String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            final Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".entity.CraftPlayer");
            final Object handle = craftPlayer.getMethod("getHandle").invoke(player);

            return (Integer) handle.getClass().getDeclaredField("ping").get(handle);
        } catch (Exception ignored) {
            return 0;
        }
    }
}
