package com.solexgames.xenon.manager;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author GrowlyX
 * @since 6/4/2021
 */

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

    private boolean disallowed;

}
