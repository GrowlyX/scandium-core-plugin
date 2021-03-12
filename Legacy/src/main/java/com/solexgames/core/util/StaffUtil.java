package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

public final class StaffUtil {

    public static void sendAlert(Player player, String reason) {
        if (CorePlugin.STAFF_ALERTS_COMMAND) {
            Bukkit.getOnlinePlayers().stream()
                    .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                    .filter(Objects::nonNull)
                    .filter(PotPlayer::isCanSeeStaffMessages)
                    .filter(potPlayer -> potPlayer.getPlayer().hasPermission(CorePlugin.getInstance().getConfig().getString("settings.staff-command-alerts-permission")))
                    .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getConfig().getString("settings.staff-command-alerts-format").replace("<playername>", player.getName()).replace("<message>", reason))));
        }
    }

    public static int getPing(Player player) {
        try {
            String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle").invoke(player);

            return (Integer) handle.getClass().getDeclaredField("ping").get(handle);
        } catch (Exception ignored) {
            return 0;
        }
    }
}
