package com.solexgames.core.server;

import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NetworkPlayer {

    private UUID uuid;

    private String name;
    private String serverName;
    private String rankName;

    private boolean receivingDms;

    public NetworkPlayer(UUID uuid, String name, String serverName, String rankName, boolean receivingDms) {
        this.uuid = uuid;
        this.name = name;
        this.serverName = serverName;
        this.rankName = rankName;
        this.receivingDms = receivingDms;

        CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().put(this.uuid, this);
    }
}
