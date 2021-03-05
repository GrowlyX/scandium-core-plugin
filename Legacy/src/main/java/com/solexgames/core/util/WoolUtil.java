package com.solexgames.core.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

public final class WoolUtil {

    private static final ArrayList<ChatColor> CHAT_COLORS = new ArrayList<>(Arrays.asList(ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.BLACK, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.BLACK));

    public static int getByColor(ChatColor color) {
        if (color.equals(ChatColor.DARK_RED)) color = ChatColor.RED;
        if (color.equals(ChatColor.DARK_BLUE)) color = ChatColor.BLUE;

        return WoolUtil.CHAT_COLORS.indexOf(color);
    }
}
