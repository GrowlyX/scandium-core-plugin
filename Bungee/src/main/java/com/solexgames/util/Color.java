package com.solexgames.util;

import net.md_5.bungee.api.ChatColor;

/**
 * @author GrowlyX
 * @since 3/5/2021
 */

public final class Color {

	public static String translate(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
