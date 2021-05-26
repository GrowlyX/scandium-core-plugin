package com.solexgames.core.player.global;

import com.google.gson.annotations.SerializedName;
import com.solexgames.core.CorePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
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

    private long connectionTime;

}
