package com.solexgames.velocity.proxy;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum ProxyStatus {

    BOOTING("Booting", "&6Booting..."),
    ONLINE("Online", "&aOnline"),
    OFFLINE("Offline", "&cOffline"),
    MAINTENANCE("Whitelisted", "&eMaintenance");

    public final String serverStatusString;
    public final String serverStatusFancyString;

    @ConstructorProperties({"serverTypeString", "serverStatusFancyString"})
    ProxyStatus(String serverStatusString, String serverStatusFancyString) {
        this.serverStatusString = serverStatusString;
        this.serverStatusFancyString = serverStatusFancyString;
    }
}
