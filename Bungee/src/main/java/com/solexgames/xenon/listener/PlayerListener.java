package com.solexgames.xenon.listener;

import com.solexgames.xenon.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerListener implements Listener {

    private final CorePlugin plugin = CorePlugin.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onProxyPing(ProxyPingEvent event) {
        if (event.getResponse() == null) {
            return;
        }

        ServerPing.Protocol responseProtocol = event.getResponse().getVersion();

        if (responseProtocol.getProtocol() < CorePlugin.getInstance().getMinProtocol()) {
            responseProtocol.setName(CorePlugin.getInstance().getMinVersion() + "+");
            responseProtocol.setProtocol(-1);

            event.getResponse().setDescription(this.plugin.getNormalMotd());
            event.getResponse().setPlayers(new ServerPing.Players(0, 1, new ServerPing.PlayerInfo[] {}));

            return;
        }

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
    public void onServerConnect(ServerConnectEvent event) {
        if ((event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY))) {
            ServerInfo hub = this.plugin.getBestHub();

            if (hub != null && hub != event.getTarget()) {
                event.setTarget(hub);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PreLoginEvent event) {
        if (event.getConnection().getVersion() < CorePlugin.getInstance().getMinProtocol()) {
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "You're on an unsupported version!\n" + ChatColor.RED + "Please connect using at least " + CorePlugin.getInstance().getMinVersion() + "!"));
            event.setCancelled(true);

            return;
        }

        if (plugin.isMaintenance() && !plugin.getWhitelistedPlayers().contains(event.getConnection().getName())) {
            event.setCancelReason(TextComponent.fromLegacyText(CorePlugin.getInstance().getMaintenanceMessage()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKick(ServerKickEvent event) {
        if (event.getCancelServer() != null && event.getCancelServer().getName() != null && !event.getCancelServer().getName().contains("hub") && !event.getKickedFrom().getName().contains("hub") && !event.getCancelServer().getName().contains("lobby") && !event.getKickedFrom().getName().contains("lobby")) {
            try {
                ServerInfo hub = CorePlugin.getInstance().getBestHub();

                if (hub == null) {
                    event.getPlayer().disconnect((new ComponentBuilder("§cCould not find a hub server to connect you to.\n§7Please contact administration if you think this is a bug.")).create());
                    return;
                }

                event.setCancelServer(hub);
                event.setCancelled(true);
                event.getPlayer().sendMessage(event.getKickReasonComponent());
            } catch (Exception ignored) {
                CorePlugin.getInstance().getProxy().getConsole().sendMessage((new ComponentBuilder("§cCouldn't find a hub server!")).create());
                event.getPlayer().disconnect((new ComponentBuilder("§cCould not find a hub server to connect you to.\n&7Please contact administration if you think this is a bug.")).create());
            }
        }
    }
}
