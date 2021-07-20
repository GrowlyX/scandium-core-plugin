package com.solexgames.xenon.listener;

import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.model.VpnRequestData;
import com.solexgames.xenon.redis.json.JsonAppender;
import com.solexgames.xenon.redis.packet.JedisAction;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {

    private final CorePlugin plugin = CorePlugin.getInstance();

    public static boolean VPN_CHECKS = true;

    @EventHandler(priority = EventPriority.HIGH)
    public void onProxyPing(ProxyPingEvent event) {
        if (event.getResponse() == null) {
            return;
        }

        final ServerPing.Protocol responseProtocol = event.getResponse().getVersion();

        if (this.plugin.isMaintenance()) {
            responseProtocol.setName("Maintenance");
            responseProtocol.setProtocol(-1);

            event.getResponse().setDescription(this.plugin.getMaintenanceMotd());
            event.getResponse().setPlayers(new ServerPing.Players(0, 1, new ServerPing.PlayerInfo[]{}));
        } else if (responseProtocol.getProtocol() < CorePlugin.getInstance().getMinProtocol() || responseProtocol.getProtocol() > CorePlugin.getInstance().getMaxProtocol()) {
            responseProtocol.setName("Xenon " + CorePlugin.getInstance().getMinVersion() + ", " + CorePlugin.getInstance().getMaxVersion());
            responseProtocol.setProtocol(-1);

            event.getResponse().setDescription(this.plugin.getNormalMotd());
        } else {
            event.getResponse().setDescription(this.plugin.getNormalMotd());
        }

        if (CorePlugin.getInstance().getXenonTopicTimer().isActive()) {
            event.getResponse().setDescription(CorePlugin.getInstance().getMotdTimerHeader() + "\n" + CorePlugin.getInstance().getMotdTimerFooter());
        }

        event.getResponse().setVersion(responseProtocol);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PreLoginEvent event) {
        if (event.getConnection().getVersion() < CorePlugin.getInstance().getMinProtocol() || event.getConnection().getVersion() > CorePlugin.getInstance().getMaxProtocol()) {
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "Please connect via " + CorePlugin.getInstance().getMinVersion() + " - " + CorePlugin.getInstance().getMaxVersion() + "."));
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isHardMaintenance() && !plugin.getHardWhitelistedPlayers().contains(event.getConnection().getName())) {
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "Sorry, the server's currently whitelisted."));
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isMaintenance() && !plugin.getWhitelistedPlayers().contains(event.getConnection().getName())) {
            event.setCancelReason(TextComponent.fromLegacyText(CorePlugin.getInstance().getMaintenanceMessage()));
            event.setCancelled(true);
            return;
        }

        boolean shouldEnableChecks = false;

        if (VPN_CHECKS) {
            final String representableIp = event.getConnection().getAddress().getAddress().toString().replace("/", "");
            final VpnRequestData requestData = CorePlugin.getInstance().getVpnManager().fetchVpnData(representableIp);

            if (requestData == null || requestData.getRequestStatus().equals("failed")) {
                event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "The API is currently offline.\n" + ChatColor.RED + "Join discord.pvp.bar for more information."));
                event.setCancelled(true);

                VPN_CHECKS = false;

                ProxyServer.getInstance().getPlayers().stream()
                        .filter(proxiedPlayer -> proxiedPlayer.hasPermission(CorePlugin.getInstance().getAlertPermission()))
                        .forEach(proxiedPlayer -> {
                            proxiedPlayer.sendMessage(ChatColor.AQUA + "[D] " + ChatColor.DARK_AQUA + "[API] " + ChatColor.RED + "The API's currently offline! VPN checks have been automatically disabled to prevent players from being stuck on login.");
                        });
                return;
            }

            if (requestData.isUsingVpn()) {
                event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "You cannot log on with a VPN!"));
                event.setCancelled(true);

                CorePlugin.getInstance().getVpnUsers().put(event.getConnection().getName(), System.currentTimeMillis());
                ProxyServer.getInstance().getPlayers().stream()
                        .filter(proxiedPlayer -> proxiedPlayer.hasPermission(CorePlugin.getInstance().getAlertPermission()))
                        .forEach(proxiedPlayer -> {
                            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(CorePlugin.getInstance().getAlertFormat(), event.getConnection().getName())));
                        });
            }

            shouldEnableChecks = true;
        }

        if (shouldEnableChecks && !VPN_CHECKS) {
            VPN_CHECKS = true;

            ProxyServer.getInstance().getPlayers().stream()
                    .filter(proxiedPlayer -> proxiedPlayer.hasPermission(CorePlugin.getInstance().getAlertPermission()))
                    .forEach(proxiedPlayer -> {
                        proxiedPlayer.sendMessage(ChatColor.AQUA + "[D] " + ChatColor.DARK_AQUA + "[API] " + ChatColor.RED + "API operations have been resumed.");
                    });
        }
    }

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        if (!event.getTarget().canAccess(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Your connection to " + ChatColor.BOLD + event.getTarget().getName() + ChatColor.RED + " has been blocked due to you not having permission to access it.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        if (event.getPlayer().hasPermission("scandium.staff")) {
            ProxyServer.getInstance().getScheduler().schedule(CorePlugin.getInstance(), new SwitchRunnable(event.getPlayer().getDisplayName(), event.getPlayer().getServer().getInfo().getName(), event.getFrom().getName()), 15L, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent event) {
        if (event.getPlayer().hasPermission("scandium.staff")) {
            ProxyServer.getInstance().getScheduler().schedule(CorePlugin.getInstance(), () -> CompletableFuture.runAsync(() -> {
                if (ProxyServer.getInstance().getPlayer(event.getPlayer().getName()) == null) {
                    CorePlugin.getInstance().getJedisManager().publish(new JsonAppender(JedisAction.PLAYER_DISCONNECT_UPDATE)
                            .put("PLAYER", event.getPlayer().getDisplayName())
                            .put("SERVER", event.getTarget().getName())
                            .getAsJson());
                }
            }), 1L, TimeUnit.SECONDS);
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
            final ServerInfo hub = this.plugin.getBestHub();

            if (hub != null && hub != event.getTarget()) {
                event.setTarget(hub);
            }

            ProxyServer.getInstance().getScheduler().schedule(CorePlugin.getInstance(), () -> {
                final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(event.getPlayer().getName());

                if (proxiedPlayer != null && proxiedPlayer.hasPermission("scandium.staff")) {
                    CompletableFuture.runAsync(() -> CorePlugin.getInstance().getJedisManager().publish(new JsonAppender(JedisAction.PLAYER_CONNECT_UPDATE)
                            .put("PLAYER", event.getPlayer().getDisplayName())
                            .put("SERVER", event.getTarget().getName())
                            .getAsJson()));
                }
            }, 2L, TimeUnit.SECONDS);
        }
    }

    @RequiredArgsConstructor
    public static class SwitchRunnable implements Runnable {

        private final String player;
        private final String target;
        private final String from;

        @Override
        public void run() {
            CompletableFuture.runAsync(() -> CorePlugin.getInstance().getJedisManager().publish(new JsonAppender(JedisAction.PLAYER_SERVER_SWITCH_UPDATE)
                    .put("PLAYER", this.player)
                    .put("SERVER", this.from)
                    .put("NEW_SERVER", this.target)
                    .getAsJson())
            );
        }
    }
}
