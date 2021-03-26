package com.solexgames.core.player.global;

import com.google.gson.annotations.SerializedName;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NetworkPlayer {

    @SerializedName("_id")
    private UUID uuid;

    private String ipAddress;

    private String name;
    private String serverName;
    private String rankName;
    private String discordCode;

    private boolean receivingDms;
    private boolean synced;

    public NetworkPlayer(UUID uuid, String name, String serverName, String rankName, boolean receivingDms, String ipAddress, String discordCode, boolean synced) {
        this.uuid = uuid;
        this.name = name;
        this.serverName = serverName;
        this.rankName = rankName;
        this.receivingDms = receivingDms;
        this.ipAddress = ipAddress;
        this.discordCode = discordCode;
        this.synced = synced;

        CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().put(this.uuid, this);
    }
}
