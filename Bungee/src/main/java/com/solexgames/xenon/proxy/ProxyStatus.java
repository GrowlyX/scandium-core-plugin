package com.solexgames.xenon.proxy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;

@Getter
@RequiredArgsConstructor
public enum ProxyStatus {

    BOOTING("Booting", "&6Booting..."),
    ONLINE("Online", "&aOnline"),
    OFFLINE("Offline", "&cOffline"),
    MAINTENANCE("Whitelisted", "&eMaintenance");

    public final String serverStatusString;
    public final String serverStatusFancyString;

}
