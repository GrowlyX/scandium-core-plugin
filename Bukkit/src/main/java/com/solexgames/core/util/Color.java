package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

public final class Color {

    public static void setup() {
        MAIN_COLOR = CorePlugin.getInstance().getServerManager().getNetwork().getMainColor().toString();
        SECONDARY_COLOR = CorePlugin.getInstance().getServerManager().getNetwork().getSecondaryColor().toString();
    }

    public static String MAIN_COLOR;
    public static String SECONDARY_COLOR;

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translate(List<String> text) {
        final List<String> newList = new ArrayList<>();

        for (String string : text) {
            newList.add(ChatColor.translateAlternateColorCodes('&', string));
        }

        return newList;
    }
}
