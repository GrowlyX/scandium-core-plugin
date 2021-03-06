package com.solexgames.listener;

import com.solexgames.CorePlugin;
import com.solexgames.util.Color;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerListener implements Listener {

    private final CorePlugin plugin = CorePlugin.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onProxyPing(ProxyPingEvent event) {
        if (event.getResponse() == null)
            return;

        ServerPing.Protocol responseProtocol = event.getResponse().getVersion();
        if (this.plugin.isMaintenance()) {
            responseProtocol.setName("Maintenance");
            responseProtocol.setProtocol(-1);

            event.getResponse().setDescription(this.plugin.getMaintenanceMotd());
            event.getResponse().setPlayers(new ServerPing.Players(0, 1, new ServerPing.PlayerInfo[] {}));
        } else {
            responseProtocol.setProtocol(1);

            event.getResponse().setDescription(this.plugin.getNormalMotd());
            event.getResponse().setPlayers(new ServerPing.Players(this.plugin.getProxy().getPlayers().size(), 1, new ServerPing.PlayerInfo[]{}));
        }
        event.getResponse().setVersion(responseProtocol);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PreLoginEvent event) {
        if (plugin.isMaintenance() && !plugin.getWhitelistedPlayers().contains(event.getConnection().getName())) {
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "The network is currently whitelisted.\nWe should be back online shortly."));
            event.setCancelled(true);
        }
    }
}
