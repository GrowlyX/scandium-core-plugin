package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.clickable.Clickable;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
                    .filter(potPlayer -> potPlayer != null && potPlayer.isCanSeeStaffMessages() && potPlayer.getPlayer().hasPermission("scandium.staff"))
                    .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(CorePlugin.getInstance().getServerSettings().getAlertFormat()
                            .replace("<playername>", player.getName())
                            .replace("<message>", reason))
                    );
        }
    }

    private static Method getHandleMethod;
    private static Field pingField;

    /**
     * Gets a player's connection ping via reflection
     * From https://www.spigotmc.org/threads/get-player-ping-with-reflection.147773/
     *
     * @param player specified player
     * @return the player's ping
     */
    public static int getPing(Player player) {
        try {
            if (getHandleMethod == null) {
                getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                getHandleMethod.setAccessible(true);
            }

            final Object entityPlayer = getHandleMethod.invoke(player);

            if (pingField == null) {
                pingField = entityPlayer.getClass().getDeclaredField("ping");
                pingField.setAccessible(true);
            }

            final int ping = pingField.getInt(entityPlayer);

            return Math.max(ping, 0);
        } catch (Exception e) {
            return 1;
        }
    }

    public static void sendTo(String message, String permission) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission(permission))
                .forEach(player -> player.sendMessage(Color.translate(message)));
    }

    public static void sendToStaff(String message) {
        Bukkit.getOnlinePlayers().stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(potPlayer -> potPlayer != null && potPlayer.isCanSeeStaffMessages() && potPlayer.getPlayer() != null && potPlayer.getPlayer().hasPermission("scandium.staff"))
                .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate(message)));
    }

    public static void sendToFiltered(String message) {
        Bukkit.getOnlinePlayers().stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(potPlayer -> potPlayer != null && potPlayer.isCanSeeFiltered() && potPlayer.getPlayer() != null && potPlayer.getPlayer().hasPermission("scandium.staff"))
                .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate(message)));
    }

    public static void sendToSocialSpy(String message) {
        Bukkit.getOnlinePlayers().stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(potPlayer -> potPlayer != null && potPlayer.isSocialSpy() && potPlayer.getPlayer() != null && potPlayer.getPlayer().hasPermission("scandium.socialspy"))
                .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate(message)));
    }

    public static void sendClickableTo(String message, String hover, String value, ClickEvent.Action action) {
        final Clickable clickable = new Clickable("");

        clickable.add(Color.translate(message), Color.translate(hover), value, action);

        Bukkit.getOnlinePlayers().stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(potPlayer -> potPlayer != null && potPlayer.isCanSeeStaffMessages() && potPlayer.getPlayer().hasPermission("scandium.staff"))
                .forEach(potPlayer -> potPlayer.getPlayer().spigot().sendMessage(clickable.asComponents()));
    }
}
