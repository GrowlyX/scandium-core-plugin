package com.solexgames.util;

import net.md_5.bungee.BungeeCord;

public final class PlayerUtil {

	public static void sendMessage(String message) {
		BungeeCord.getInstance().getPlayers().forEach(proxiedPlayer -> proxiedPlayer.sendMessage(Color.translate(message)));
	}

	public static void sendMessage(String message, String permission) {
		BungeeCord.getInstance().getPlayers()
				.stream()
				.filter(proxiedPlayer -> proxiedPlayer.hasPermission(permission))
				.forEach(proxiedPlayer -> proxiedPlayer.sendMessage(Color.translate(message)));
	}
}
