package com.solexgames.xenon.redis.handler.impl;

import com.solexgames.xenon.redis.annotation.Subscription;
import com.solexgames.xenon.redis.handler.JedisHandler;
import com.solexgames.xenon.redis.json.JsonAppender;

import java.util.UUID;

@SuppressWarnings("unused")
public class JedisListener implements JedisHandler {

    @Subscription(action = "GLOBAL_PLAYER_REMOVE")
    public void onGlobalPlayerRemove(JsonAppender jsonAppender) {
        final UUID removingPlayer = UUID.fromString(jsonAppender.getParam("UUID"));
        final String removalServer = jsonAppender.getParam("SERVER");

//        if (!removalServer.equalsIgnoreCase(CorePlugin.getInstance().getServerName())) {
//            CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().remove(CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(removingPlayer));
//        }
    }

    @Subscription(action = "GLOBAL_PLAYER_ADDITION")
    public void onGlobalPlayerAddition(JsonAppender jsonAppender) {
//        final UUID uuid = UUID.fromString(jsonAppender.getParam("UUID"));
//        final String name = jsonAppender.getParam("NAME");
//        final Rank rank = Rank.getByName(jsonAppender.getParam("RANK"));
//        final String globalServer = jsonAppender.getParam("SERVER");
//        final String syncCode = jsonAppender.getParam("SYNC_CODE");
//
//        final boolean dmsEnabled = Boolean.parseBoolean(jsonAppender.getParam("DMS_ENABLED"));
//        final boolean isSynced = Boolean.parseBoolean(jsonAppender.getParam("IS_SYNCED"));
//
//        final NetworkPlayer networkPlayer = new NetworkPlayer(uuid, "", name, globalServer, rank.getName(), syncCode, dmsEnabled, isSynced, System.currentTimeMillis());
//
//        if (CorePlugin.getInstance().getPlayerManager().isOnline(name)) {
//            final NetworkPlayer oldPlayer = CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(name);
//
//            oldPlayer.setServerName(globalServer);
//            oldPlayer.setSynced(isSynced);
//        } else {
//            CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().add(networkPlayer);
//        }
//
//        if (!CorePlugin.getInstance().getUuidCache().containsValue(uuid)) {
//            CorePlugin.getInstance().getUuidCache().put(name, uuid);
//        }
    }
}
