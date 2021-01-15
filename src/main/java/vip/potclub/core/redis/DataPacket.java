package vip.potclub.core.redis;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum DataPacket {

    CHAT_CHANNEL_UPDATE("ChatChannelUpdate"),
    PLAYER_SERVER_UPDATE("PlayerServerUpdate"),
    NETWORK_BROADCAST_UPDATE("NetworkBroadcastUpdate"),
    ;

    public String packetDataName;

    @ConstructorProperties("packetDataName")
    DataPacket(String packetDataName) {
        this.packetDataName = packetDataName;
    }
}
