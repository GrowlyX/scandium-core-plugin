package com.solexgames.xenon.redis.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
@AllArgsConstructor
public enum RedisAction {

    PROXY_DATA_ONLINE("ServerDataOnline"),
    PROXY_DATA_UPDATE("ServerDataUpdate"),
    PROXY_SERVER_DATA_OFFLINE("ServerDataOffline"),

    PLAYER_SERVER_SWITCH_UPDATE("PlayerServerSwitchUpdate"),
    PLAYER_CONNECT_UPDATE("PlayerConnectUpdate"),
    PLAYER_DISCONNECT_UPDATE("PlayerDisconnectUpdate");

    private final String packetDataName;

}
