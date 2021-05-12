package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.solexgames.xenon.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;

@CommandAlias("hub|lobby|l")
public class HubCommand extends BaseCommand {

    @Default
    public void onHub(ProxiedPlayer player) {
        if (!player.getServer().getInfo().getName().contains("hub") && !player.getServer().getInfo().getName().contains("lobby")) {
            final ServerInfo hub = CorePlugin.getInstance().getBestHub();

            if (hub != null) {
                player.connect(hub, ServerConnectEvent.Reason.COMMAND);
            } else {
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "There aren't any available hubs right now!"));
            }
        } else {
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "I'm sorry, but you're already in a hub."));
        }
    }
}
