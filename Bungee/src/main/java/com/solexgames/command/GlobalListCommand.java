package com.solexgames.command;

import com.solexgames.util.Color;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Collection;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * @author GrowlyX
 * @since 3/5/2021
 */

public class GlobalListCommand extends Command {

	public GlobalListCommand() {
		super("glist", null, "globallist");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(TextComponent.fromLegacyText("You can only use this command as a player."));
		} else {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if (args.length == 0) {
				player.sendMessage(Color.translate("&bThere " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "is " : "are ") + "currently &3" + BungeeCord.getInstance().getPlayers().size() + " " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "Player " : "Players ") + "&bon the network."));
				player.sendMessage(Color.translate("&7&oTIP: Use /glist all to show a list of all online players on each server."));
			} else if (args.length == 1 && args[0].equals("all")) {
				player.sendMessage(Color.translate("&bThere " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "is " : "are ") + "currently &3" + BungeeCord.getInstance().getPlayers().size() + " " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "Player " : "Players ") + "&bon the network."));
				player.sendMessage(Color.translate("  "));

				ProxyServer.getInstance().getServers().values().forEach((serverInfo) -> {
					Collection<ProxiedPlayer> proxiedPlayerCollection = serverInfo.getPlayers();
					ArrayList<ProxiedPlayer> proxiedPlayerArrayList = new ArrayList<>(proxiedPlayerCollection);
					StringBuilder builder = new StringBuilder();

					proxiedPlayerArrayList.forEach((proxiedPlayer) -> builder.append(WHITE).append(proxiedPlayer.getName()).append(GRAY).append(", "));

					player.sendMessage(Color.translate("&3[" + serverInfo.getName() + "]&7: &f" + builder));
				});

				player.sendMessage(" ");
			}
		}
	}
}
