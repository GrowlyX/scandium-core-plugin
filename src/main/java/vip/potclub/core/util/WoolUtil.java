package vip.potclub.core.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;

public final class WoolUtil {

    public static final ArrayList<ChatColor> woolColors = (ArrayList<ChatColor>) Arrays.asList(
            ChatColor.WHITE,
            ChatColor.GOLD,
            ChatColor.LIGHT_PURPLE,
            ChatColor.AQUA,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.LIGHT_PURPLE,
            ChatColor.DARK_GRAY,
            ChatColor.GRAY,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_PURPLE,
            ChatColor.BLUE,
            ChatColor.RESET,
            ChatColor.DARK_GREEN,
            ChatColor.RED,
            ChatColor.BLACK
    );

    public static int getByColor(ChatColor color) {
        if(color == ChatColor.DARK_RED) color = ChatColor.RED;
        if (color == ChatColor.DARK_BLUE) color = ChatColor.BLUE;

        return WoolUtil.woolColors.indexOf(color);
    }
}
