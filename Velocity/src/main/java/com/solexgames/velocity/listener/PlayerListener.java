package com.solexgames.velocity.listener;

import com.solexgames.velocity.CorePlugin;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;

public class PlayerListener {

    /*private final CorePlugin plugin = CorePlugin.getInstance();

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        if (event.getResponse() == null) {
            return;
        }

        ServerPing.Version responseProtocol = event.getPing().getVersion();

        if (this.plugin.isMaintenance()) {
            responseProtocol.setName("Maintenance");
            responseProtocol.setProtocol(-1);

            event.getResponse().setDescription(this.plugin.getMaintenanceMotd());
            event.getResponse().setPlayers(new ServerPing.Players(0, 1, new ServerPing.PlayerInfo[] {}));
        } else {
            event.getResponse().setDescription(this.plugin.getNormalMotd());
        }

        event.getResponse().setVersion(responseProtocol);
    }

    @EventHandler
    public void onServerConnect(ServerPreConnectEvent event) {
        if ((event.getReason().equals(ServerPreConnectEvent.Res.JOIN_PROXY))) {
            ServerInfo hub = this.plugin.getBestHub();

            if (hub != null && hub != event.getTarget()) {
                event.setTarget(hub);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PreLoginEvent event) {
        if (plugin.isMaintenance() && !plugin.getWhitelistedPlayers().contains(event.getConnection().getName())) {
            event.setCancelReason(TextComponent.fromLegacyText(CorePlugin.getInstance().getMaintenanceMessage()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKick(KickedFromServerEvent event) {
        if (event.getServer() != null && event.getServer().getServerInfo().getName() != null && !event.getServer().getServerInfo().getName().contains("hub")) {
            try {
                RegisteredServer hub = CorePlugin.getInstance().getBestHub();

                if (hub == null) {
                    event.getPlayer().disconnect(Component.text("§cCould not find a hub server to connect you to.\n§7Please contact administration if you think this is a bug."));
                    return;
                }



                if (event.getServerKickReason().orElse(null) != null) {
                    event.getPlayer().sendMessage(event.getServerKickReason().orElse(null));
                }
            } catch (Exception ignored) {
                CorePlugin.getInstance().getProxy().getConsole().sendMessage((new ComponentBuilder("§cCouldn't find a hub server!")).create());
                event.getPlayer().disconnect((new ComponentBuilder("§cCould not find a hub server to connect you to.\n&7Please contact administration if you think this is a bug.")).create());
            }
        }
    }*/
}
