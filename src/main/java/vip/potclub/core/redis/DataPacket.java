package vip.potclub.core.redis;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum DataPacket {

    CHAT_CHANNEL_UPDATE("ChatChannelUpdate"),

    PLAYER_FREEZE_UPDATE("PlayerFreezeUpdate"),
    PLAYER_REPORT_UPDATE("PlayerReportUpdate"),
    PLAYER_HELPOP_UPDATE("PlayerHelpopUpdate"),
    PLAYER_ONCOMMAND_UPDATE("PlayerOnCommandUpdate"),

    NETWORK_BROADCAST_UPDATE("NetworkBroadcastUpdate"),
    ;

    public String packetDataName;

    @ConstructorProperties("packetDataName")
    DataPacket(String packetDataName) {
        this.packetDataName = packetDataName;
    }
}
