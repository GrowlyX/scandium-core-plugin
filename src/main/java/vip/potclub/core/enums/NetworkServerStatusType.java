package vip.potclub.core.enums;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum NetworkServerStatusType {

    BOOTING("Booting", "&6Booting..."),
    ONLINE("Online", "&aOnline"),
    OFFLINE("Offline", "&cOffline"),
    WHITELISTED("Whitelisted", "&eWhitelisted");

    public final String serverStatusString;
    public final String serverStatusFancyString;

    @ConstructorProperties({"serverTypeString", "serverStatusFancyString"})
    NetworkServerStatusType(String serverStatusString, String serverStatusFancyString) {
        this.serverStatusString = serverStatusString;
        this.serverStatusFancyString = serverStatusFancyString;
    }
}
