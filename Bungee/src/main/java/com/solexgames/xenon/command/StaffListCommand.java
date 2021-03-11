package com.solexgames.xenon.command;

import com.solexgames.xenon.util.Color;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Collection;

import static net.md_5.bungee.api.ChatColor.GRAY;
import static net.md_5.bungee.api.ChatColor.WHITE;

/**
 * @author GrowlyX
 * @since 3/5/2021
 */

public class StaffListCommand extends Command {

	public StaffListCommand() {
		super("slist", null, "stafflist");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(TextComponent.fromLegacyText("Only players can execute this command."));
		} else {
			ProxiedPlayer player = (ProxiedPlayer)sender;

			if (player.hasPermission("xenon.network.staff")) {
				if (args.length == 0) {
					player.sendMessage(TextComponent.fromLegacyText(Color.translate("&bThere " + (this.getStaffSize() == 1 ? "is " : "are ") + "currently &3" + this.getStaffSize() + " Staff " + (this.getStaffSize() == 1 ? "Member " : "Members") + " &bon the network.")));
					player.sendMessage(Color.translate("&7&oTIP: Use /glist all to show a list of all online staff on each server."));
				} else if (args.length == 1 && args[0].equals("all")) {
					player.sendMessage(Color.translate("&bThere " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "is " : "are ") + "currently &3" + BungeeCord.getInstance().getPlayers().size() + " " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "Player " : "Players ") + "&bon the network."));
					player.sendMessage(Color.translate("  "));

					ProxyServer.getInstance().getServers().values().forEach((serverInfo) -> {
						Collection<ProxiedPlayer> proxiedPlayerCollection = serverInfo.getPlayers();
						ArrayList<ProxiedPlayer> proxiedPlayerArrayList = new ArrayList<>(proxiedPlayerCollection);

						StringBuilder builder = new StringBuilder();
						proxiedPlayerArrayList.stream()
								.filter(proxiedPlayer -> proxiedPlayer.hasPermission("xenon.network.staff"))
								.forEach((proxiedPlayer) -> builder.append(WHITE).append(proxiedPlayer.getName()).append(GRAY).append(", "));

						player.sendMessage(Color.translate("&3[" + serverInfo.getName() + "]&7: &f" + builder));
					});

					player.sendMessage(" ");
				}
			} else {
				player.sendMessage(NO_PERMISSION);
			}
		}
	}

	private int getStaffSize() {
		return (int) BungeeCord.getInstance().getPlayers().stream()
				.filter(proxiedPlayer -> proxiedPlayer.hasPermission("xenon.network.staff"))
				.count();
	}
}
