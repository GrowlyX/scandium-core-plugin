package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import org.bukkit.entity.Player;

public final class LoggerUtil {

    public static void logChat(Player player, String message) {
        CorePlugin.getInstance().getChatLogger().info(player.getName() + ": " + message);
    }

    public static void logCommand(Player player, String command) {
        CorePlugin.getInstance().getChatLogger().info(player.getName() + " executed \"" + command + "\"");
    }
}
