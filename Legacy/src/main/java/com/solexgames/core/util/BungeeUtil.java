package com.solexgames.core.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author GrowlyX
 * @since March 2021
 */

@UtilityClass
public final class BungeeUtil {

    /**
     * Connects a player to a server
     * <p>
     *
     * @param player       Player to connect to a server
     * @param connectingTo Connecting server
     * @param javaPlugin   Java plugin which has the BungeeCord messaging channel registered.
     */
    public static void sendToServer(Player player, String connectingTo, JavaPlugin javaPlugin) {
        try {
            final ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();

            byteArrayDataOutput.writeUTF("Connect");
            byteArrayDataOutput.writeUTF(connectingTo);

            player.sendPluginMessage(javaPlugin, "BungeeCord", byteArrayDataOutput.toByteArray());
        } catch (Exception ignored) {
            player.sendMessage(ChatColor.RED + "Something went wrong while sending you to \"" + ChatColor.YELLOW + connectingTo + ChatColor.RED + "\".");
        }
    }
}
