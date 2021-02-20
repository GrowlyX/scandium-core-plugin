package vip.potclub.core.util;

import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;

public final class LoggerUtil {

    public static void logChat(Player player, String message) {
        CorePlugin.getInstance().getChatLogger().info(player.getName() + ": " + message);
    }

    public static void logCommand(Player player, String command) {
        CorePlugin.getInstance().getChatLogger().info(player.getName() + " executed \"" + command + "\"");
    }
}
