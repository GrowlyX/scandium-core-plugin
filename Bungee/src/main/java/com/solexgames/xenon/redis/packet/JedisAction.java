package com.solexgames.xenon.redis.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author GrowlyX
 * @since 1/1/2021
 */

@Getter
@RequiredArgsConstructor
public enum JedisAction {

    PLAYER_SERVER_UPDATE("PlayerServerUpdate"),
    PLAYER_SERVER_SWITCH_UPDATE("PlayerServerSwitchUpdate"),
    PLAYER_CONNECT_UPDATE("PlayerConnectUpdate"),
    PLAYER_DISCONNECT_UPDATE("PlayerDisconnectUpdate"),

    GLOBAL_PLAYER_ADDITION("GlobalPlayerAddition"),
    GLOBAL_PLAYER_REMOVE("GlobalPlayerRemove");

    private final String packetDataName;

}
