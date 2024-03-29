package com.solexgames.xenon.redis.handler.impl;

import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.manager.NetworkPlayer;
import com.solexgames.xenon.redis.annotation.Subscription;
import com.solexgames.xenon.redis.handler.JedisHandler;
import com.solexgames.xenon.redis.json.JsonAppender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class JedisListener implements JedisHandler {

    @Subscription(action = "PERMISSION_SYNC")
    public void onPermissionSync(JsonAppender jsonAppender) {
        final String username = jsonAppender.getParam("PLAYER");
        final String displayName = jsonAppender.getParam("DISPLAY");
        final String combinedPermissions = jsonAppender.getParam("PERMISSIONS");

        final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(username);

        if (player != null) {
            final String[] permissions = combinedPermissions.split(":");

            for (String permission : permissions) {
                player.setPermission(permission, true);
            }

            player.setDisplayName(displayName);
        }
    }

    @Subscription(action = "MAINTENANCE_ADD")
    public void onMaintenanceAdd(JsonAppender jsonAppender) {
        final String addingPlayer = jsonAppender.getParam("PLAYER");

        if (!CorePlugin.getInstance().getWhitelistedPlayers().contains(addingPlayer)) {
            CorePlugin.getInstance().getWhitelistedPlayers().add(addingPlayer);
        }

        System.out.println("[Maintenance Update] Added " + addingPlayer + " to maintenance");
    }

    @Subscription(action = "MAINTENANCE_REMOVE")
    public void onMaintenanceRemove(JsonAppender jsonAppender) {
        final String addingPlayer = jsonAppender.getParam("PLAYER");
        CorePlugin.getInstance().getWhitelistedPlayers().remove(addingPlayer);

        System.out.println("[Maintenance Update] Removed " + addingPlayer + " from maintenance");
    }

    @Subscription(action = "MAINTENANCE_UPDATE")
    public void onMaintenanceUpdate(JsonAppender jsonAppender) {
        final boolean togglingStatus = Boolean.parseBoolean(jsonAppender.getParam("TYPE"));

        if (togglingStatus) {
            System.out.println("[Maintenance Update] Maintenance has been enabled.");

            ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer1 -> {
                if (!CorePlugin.getInstance().getWhitelistedPlayers().contains(proxiedPlayer1.getName())) {
                    proxiedPlayer1.disconnect(ChatColor.RED + "Sorry, but the server's now in maintenance.");
                }
            });
        } else {
            System.out.println("[Maintenance Update] Maintenance has been disabled.");
        }

        CorePlugin.getInstance().setMaintenance(togglingStatus);
    }

    @Subscription(action = "GLOBAL_PLAYER_REMOVE")
    public void onGlobalPlayerRemove(JsonAppender jsonAppender) {
        final UUID removingPlayer = UUID.fromString(jsonAppender.getParam("UUID"));
        final String removalServer = jsonAppender.getParam("SERVER");

        final List<NetworkPlayer> networkPlayers = new ArrayList<>(CorePlugin.getInstance().getNetworkPlayerManager().getAllNetworkProfiles());

        networkPlayers.stream().filter(nPlayer -> nPlayer.getUuid().equals(removingPlayer))
                .findFirst().ifPresent(networkPlayer -> CorePlugin.getInstance().getNetworkPlayerManager().getAllNetworkProfiles().remove(networkPlayer));
    }

    @Subscription(action = "GLOBAL_PLAYER_ADDITION")
    public void onGlobalPlayerAddition(JsonAppender jsonAppender) {
        final UUID uuid = UUID.fromString(jsonAppender.getParam("UUID"));
        final String name = jsonAppender.getParam("NAME");
        final String rank = jsonAppender.getParam("RANK");
        final String globalServer = jsonAppender.getParam("SERVER");
        final String syncCode = jsonAppender.getParam("SYNC_CODE");
        final boolean disallowed = Boolean.parseBoolean(jsonAppender.getParam("DISALLOWED"));

        final boolean dmsEnabled = Boolean.parseBoolean(jsonAppender.getParam("DMS_ENABLED"));
        final boolean isSynced = Boolean.parseBoolean(jsonAppender.getParam("IS_SYNCED"));

        final NetworkPlayer networkPlayer = new NetworkPlayer(uuid, "", name, globalServer, rank, syncCode, dmsEnabled, isSynced, System.currentTimeMillis(), disallowed);

        CorePlugin.getInstance().getNetworkPlayerManager().getAllNetworkProfiles().add(networkPlayer);
    }
}
