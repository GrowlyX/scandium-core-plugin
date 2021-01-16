package vip.potclub.core.enums;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum DataPacket {

    CHAT_CHANNEL_UPDATE("ChatChannelUpdate"),
    PLAYER_SERVER_UPDATE("PlayerServerUpdate"),
    PLAYER_CONNECT_UPDATE("PlayerConnectUpdate"),
    PLAYER_DISCONNECT_UPDATE("PlayerDisconnectUpdate"),
    NETWORK_BROADCAST_UPDATE("NetworkBroadcastUpdate");

    public String packetDataName;

    @ConstructorProperties("packetDataName")
    DataPacket(String packetDataName) {
        this.packetDataName = packetDataName;
    }
}
