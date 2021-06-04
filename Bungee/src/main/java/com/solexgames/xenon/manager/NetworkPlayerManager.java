package com.solexgames.xenon.manager;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 6/4/2021
 */

@Getter
public class NetworkPlayerManager {

    public final List<NetworkPlayer> allNetworkProfiles = new ArrayList<>();

    public NetworkPlayer getByUuid(UUID uuid) {
        return this.allNetworkProfiles.stream()
                .filter(networkPlayer -> networkPlayer.getUuid().toString().equals(uuid.toString()))
                .findFirst().orElse(null);
    }
}
