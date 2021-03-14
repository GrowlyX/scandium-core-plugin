package com.solexgames.core.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Joelioli
 * @since ???
 */

public final class BungeeUtil {

    private BungeeUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static void sendToServer(Player player, String connectingTo, JavaPlugin javaPlugin) {
        try {
            ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();

            byteArrayDataOutput.writeUTF("Connect");
            byteArrayDataOutput.writeUTF(connectingTo);

            player.sendPluginMessage(javaPlugin, "BungeeCord", byteArrayDataOutput.toByteArray());
        } catch (Exception ignored) {
            player.sendMessage(ChatColor.RED + "Something went wrong while sending you to \"" + ChatColor.YELLOW + connectingTo + ChatColor.RED + "\".");
        }
    }
}
