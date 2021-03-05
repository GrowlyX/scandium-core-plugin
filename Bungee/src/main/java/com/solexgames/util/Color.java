package com.solexgames.util;

import net.md_5.bungee.api.ChatColor;

public final class Color {

	public static String translate(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
