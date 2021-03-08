package com.solexgames.proxy;

import com.solexgames.CorePlugin;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Proxy {

    private List<String> allPlayers = new ArrayList<>();

    private String region;
    private String ticksPerSecond;
    private String ticksPerSecondSimplified;

    private ProxyStatus serverStatus;

    private int maxPlayerLimit;
    private int onlinePlayers;

    private boolean whitelistEnabled;

    /**
     * Create a new instance of {@link Proxy}
     *
     * @param region Proxy region
     */
    public Proxy(String region) {
        this.region = region;
    }

    /**
     * Update this instance of {@link Proxy}
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
            this.serverStatus = ProxyStatus.MAINTENANCE;
        } else if (online) {
            this.serverStatus = ProxyStatus.ONLINE;
        } else {
            this.serverStatus = ProxyStatus.OFFLINE;
        }
    }
}
