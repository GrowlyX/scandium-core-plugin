package com.solexgames.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;

@Getter
@RequiredArgsConstructor
public enum NetworkServerStatusType {

    BOOTING("Booting", "&6Booting..."),
    ONLINE("Online", "&aOnline"),
    OFFLINE("Offline", "&cOffline"),
    WHITELISTED("Whitelisted", "&eWhitelisted");

    public final String serverStatusString;
    public final String serverStatusFancyString;

}
