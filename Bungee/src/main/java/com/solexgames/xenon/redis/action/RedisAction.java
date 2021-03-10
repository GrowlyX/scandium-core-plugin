package com.solexgames.xenon.redis.action;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum RedisAction {

    PROXY_DATA_ONLINE("ServerDataOnline"),
    PROXY_DATA_UPDATE("ServerDataUpdate"),
    PROXY_SERVER_DATA_OFFLINE("ServerDataOffline");

    private final String packetDataName;

    @ConstructorProperties("packetDataName")
    RedisAction(String packetDataName) {
        this.packetDataName = packetDataName;
    }
}
