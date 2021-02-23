package com.solexgames.core.server;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.NetworkServerStatusType;
import com.solexgames.core.enums.NetworkServerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetworkServer {

    private String serverName;
    private String ticksPerSecond;
    private String ticksPerSecondSimplified;

    private NetworkServerType serverType;
    private NetworkServerStatusType serverStatus;

    private int maxPlayerLimit;
    private int onlinePlayers;

    private boolean whitelistEnabled;

    public NetworkServer(String serverName, NetworkServerType serverType) {
        this.serverName = serverName;
        this.serverType = serverType;

        CorePlugin.getInstance().getServerManager().addNetworkServer(this);
    }

    public static NetworkServer getByName(String name){
        return CorePlugin.getInstance().getServerManager().getNetworkServers().stream().filter(server -> server.getServerName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void update(int onlinePlayers, String ticksPerSecond, int maxPlayerLimit, boolean whitelistEnabled, String ticksPerSecondSimplified, boolean online) {
        this.onlinePlayers = onlinePlayers;
        this.ticksPerSecond = ticksPerSecond;
        this.maxPlayerLimit = maxPlayerLimit;
        this.whitelistEnabled = whitelistEnabled;
        this.ticksPerSecondSimplified = ticksPerSecondSimplified;
        updateServerStatus(online, whitelistEnabled);
    }

    public void updateServerStatus(boolean online, boolean whitelistEnabled) {
        if (whitelistEnabled && online) {
            this.serverStatus = NetworkServerStatusType.WHITELISTED;
        } else if (online) {
            this.serverStatus = NetworkServerStatusType.ONLINE;
        } else {
            this.serverStatus = NetworkServerStatusType.OFFLINE;
        }
    }
}
