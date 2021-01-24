package vip.potclub.core.enums;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum RedisPacketType {

    CHAT_CHANNEL_UPDATE("ChatChannelUpdate"),
    PLAYER_SERVER_UPDATE("PlayerServerUpdate"),
    PLAYER_CONNECT_UPDATE("PlayerConnectUpdate"),
    PLAYER_DISCONNECT_UPDATE("PlayerDisconnectUpdate"),
    PLAYER_DISCORD_SYNC("PlayerDiscordSync"),
    NETWORK_BROADCAST_UPDATE("NetworkBroadcastUpdate");

    public String packetDataName;

    @ConstructorProperties("packetDataName")
    RedisPacketType(String packetDataName) {
        this.packetDataName = packetDataName;
    }
}
