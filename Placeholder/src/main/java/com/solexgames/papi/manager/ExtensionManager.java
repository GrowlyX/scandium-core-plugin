package com.solexgames.papi.manager;

import com.solexgames.core.enums.NetworkServerStatusType;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.papi.extension.ServerExtension;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ExtensionManager {

    private final Map<NetworkServer, ServerExtension> placeholders = new ConcurrentHashMap<>();

    public void addPlaceholder(NetworkServer server, ServerExtension extension) {
        this.placeholders.put(server, extension);
    }

    public void removePlaceholder(NetworkServer server) {
        ServerExtension extension = this.placeholders.get(server);

        if (extension != null) {
            this.placeholders.remove(server);

            if (extension.isRegistered()) {
                extension.unregister();
            }
        }
    }

    public String getJoinableStatus(NetworkServer server) {
        if (server == null) {
            return ChatColor.RED + "You cannot join this server.";
        }

        if (server.getServerStatus() == NetworkServerStatusType.ONLINE) {
            return ChatColor.GREEN + "Online";
        }

        return ChatColor.RED + "You cannot join this server.";
    }

    public String getShortStatus(NetworkServer server) {
        if (server == null) {
            return ChatColor.RED + "Offline";
        }

        switch (server.getServerStatus()) {
            case BOOTING:
                return ChatColor.GOLD + "Booting...";
            case WHITELISTED:
                return ChatColor.YELLOW + "Whitelisted";
            case ONLINE:
                return ChatColor.GREEN + "Online";
            default:
                return ChatColor.RED + "Offline";
        }
    }

    public String getFancyStatus(NetworkServer server) {
        if (server == null) {
            return ChatColor.RED + "Server offline";
        }

        switch (server.getServerStatus()) {
            case BOOTING:
                return ChatColor.GOLD + "Server booting";
            case WHITELISTED:
                return ChatColor.YELLOW + "Server whitelisted";
            case ONLINE:
                return ChatColor.GREEN + "Server online";
            default:
                return ChatColor.RED + "Server offline";
        }
    }
}
