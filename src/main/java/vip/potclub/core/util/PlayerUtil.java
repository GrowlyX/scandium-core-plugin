package vip.potclub.core.util;

import org.bukkit.entity.Player;
import vip.potclub.kotlin.clickable.ChatClickable;

public final class PlayerUtil {

    private PlayerUtil() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate a utility class");
    }

    public static void sendClickable(Player player, ChatClickable chatClickable) {
        player.spigot().sendMessage(chatClickable.asComponents());
    }
}
