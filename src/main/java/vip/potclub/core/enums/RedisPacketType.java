package vip.potclub.core.enums;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum RedisPacketType {

    CHAT_CHANNEL_UPDATE("ChatChannelUpdate"),
    PLAYER_SERVER_UPDATE("PlayerServerUpdate"),
    PLAYER_CONNECT_UPDATE("PlayerConnectUpdate"),
    PLAYER_DISCONNECT_UPDATE("PlayerDisconnectUpdate"),
    PLAYER_MESSAGE_UPDATE("PlayerMessageUpdate"),
    PLAYER_DISCORD_SYNC("PlayerDiscordSync"),
    SERVER_DATA_COMMAND("ServerDataCommand"),
    SERVER_DATA_ONLINE("ServerDataOnline"),
    SERVER_DATA_UPDATE("ServerDataUpdate"),
    SERVER_DATA_OFFLINE("ServerDataOffline"),
    NETWORK_BROADCAST_PERMISSION_UPDATE("NetworkBroadcastPermissionUpdate"),
    NETWORK_BROADCAST_UPDATE("NetworkBroadcastUpdate");

    private final String packetDataName;

    @ConstructorProperties("packetDataName")
    RedisPacketType(String packetDataName) {
        this.packetDataName = packetDataName;
    }
}
