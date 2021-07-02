package com.solexgames.xenon.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author GrowlyX
 * @since 7/1/2021
 */

@Data
public class VpnRequestData {

    // success, failed
    @SerializedName("status")
    private String requestStatus;

    @SerializedName("msg")
    private String requestFailedMessage;

    @SerializedName("host-ip")
    private boolean usingVpn;

    @SerializedName("org")
    private String organization;

}
