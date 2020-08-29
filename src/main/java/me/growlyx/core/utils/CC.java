package me.growlyx.core.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class CC {

    public static String line;

    public static String translate(String text) {
        String output = text;
        return ChatColor.translateAlternateColorCodes('&', output);
    }

    public static String line(String text) {
        return CC.translate("&7&m--------------------------------------");
    }

    public static List<String> translate(List<String> list) {
        return list.stream().map(CC::translate).collect(Collectors.toList());
    }

}
