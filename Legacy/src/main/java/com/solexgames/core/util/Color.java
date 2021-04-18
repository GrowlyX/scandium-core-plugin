package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

public final class Color {

    public static ChatColor MAIN_COLOR;
    public static ChatColor SECONDARY_COLOR;

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translate(List<String> text) {
        return text.stream().map(Color::translate).collect(Collectors.toList());
    }

    /**
     * Sets up custom colors via the config
     */
    public void setupMessages() {
        MAIN_COLOR = CorePlugin.getInstance().getServerManager().getNetwork().getMainColor();
        SECONDARY_COLOR = CorePlugin.getInstance().getServerManager().getNetwork().getSecondaryColor();
    }
}
