package com.solexgames.core.redis.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;

/**
 * @author GrowlyX
 * @since 1/1/2021
 */

@Getter
@RequiredArgsConstructor
public enum JedisAction {

    CHAT_CHANNEL_UPDATE("ChatChannelUpdate"),

    PLAYER_SERVER_UPDATE("PlayerServerUpdate"),
    PLAYER_SERVER_SWITCH_UPDATE("PlayerServerSwitchUpdate"),
    PLAYER_CONNECT_UPDATE("PlayerConnectUpdate"),
    PLAYER_DISCONNECT_UPDATE("PlayerDisconnectUpdate"),

    GLOBAL_PLAYER_ADDITION("GlobalPlayerAddition"),
    GLOBAL_PLAYER_REMOVE("GlobalPlayerRemove"),
    GLOBAL_PLAYER_MESSAGE("GlobalPlayerMessage"),

    SERVER_DATA_ONLINE("ServerDataOnline"),
    SERVER_DATA_UPDATE("ServerDataUpdate"),
    SERVER_DATA_OFFLINE("ServerDataOffline"),

    NETWORK_BROADCAST_PERMISSION_UPDATE("NetworkBroadcastPermissionUpdate"),
    NETWORK_BROADCAST_UPDATE("NetworkBroadcastUpdate"),

    PUNISHMENT_EXECUTE_UPDATE("PunishmentExecuteUpdate"),
    PUNISHMENT_REMOVE_UPDATE("PunishmentRemoveUpdate"),
    PUNISHMENT_F_REMOVE_UPDATE("PunishmentFRemoveUpdate"),

    RANK_SETTINGS_UPDATE("RankSettingsUpdate"),
    RANK_CREATE_UPDATE("RankCreateUpdate"),
    RANK_DELETE_UPDATE("RankDeleteUpdate"),

    PREFIX_CREATE_UPDATE("PrefixCreateUpdate"),
    PREFIX_DELETE_UPDATE("PrefixDeleteUpdate"),
    PREFIX_SETTINGS_UPDATE("PrefixSettingsUpdate"),

    DISCORD_SYNC_UPDATE("DiscordSyncUpdate"),

    DISGUISE_PROFILE_CREATE("DisguiseProfileCreate"),
    DISGUISE_PROFILE_REMOVE("DisguiseProfileRemove");

    private final String packetDataName;

}
