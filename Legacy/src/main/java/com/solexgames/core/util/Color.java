package com.solexgames.core.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public final class Color {

    public static String translate(String text) {
        return Color.translate(text);
    }

    public static List<String> translate(List<String> text) {
        return text.stream().map(Color::translate).collect(Collectors.toList());
    }
}
