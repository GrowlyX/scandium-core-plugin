package com.solexgames.core.server;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.NetworkServerStatusType;
import com.solexgames.core.enums.NetworkServerType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class NetworkServer {

    private List<String> allPlayers = new ArrayList<>();

    private String serverName;
    private String ticksPerSecond;
    private String ticksPerSecondSimplified;

    private NetworkServerType serverType;
    private NetworkServerStatusType serverStatus;

    private int maxPlayerLimit;
    private int onlinePlayers;

    private boolean whitelistEnabled;

    /**
     * Create a new instance of {@link NetworkServer}
     *
     * @param serverName ServerType Server name
     * @param serverType ServerType Server type
     */
    public NetworkServer(String serverName, NetworkServerType serverType) {
        this.serverName = serverName;
        this.serverType = serverType;

        CorePlugin.getInstance().getServerManager().addNetworkServer(this);
    }

    public static NetworkServer getByName(String name){
        return CorePlugin.getInstance().getServerManager().getNetworkServers().stream().filter(server -> server.getServerName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Update this instance of {@link NetworkServer}
     *
     * @param onlinePlayers Online Player Count
     * @param ticksPerSecond TPS
     * @param maxPlayerLimit Max Players
     * @param whitelistEnabled Whitelist
     * @param ticksPerSecondSimplified Simplified TPS
     * @param online Online or offline
     */
    public void update(int onlinePlayers, String ticksPerSecond, int maxPlayerLimit, boolean whitelistEnabled, String ticksPerSecondSimplified, boolean online, String onlinePlayersSplit) {
        this.onlinePlayers = onlinePlayers;
        this.ticksPerSecond = ticksPerSecond;
        this.maxPlayerLimit = maxPlayerLimit;
        this.whitelistEnabled = whitelistEnabled;
        this.ticksPerSecondSimplified = ticksPerSecondSimplified;
        this.allPlayers = (onlinePlayersSplit == null || onlinePlayersSplit.equals("")) ? Collections.singletonList("") : Arrays.asList(onlinePlayersSplit.split(" "));

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
