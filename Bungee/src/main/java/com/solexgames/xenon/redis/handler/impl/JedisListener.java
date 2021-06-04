package com.solexgames.xenon.redis.handler.impl;

import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.manager.NetworkPlayer;
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

        CorePlugin.getInstance().getNetworkPlayerManager().getAllNetworkProfiles()
                .remove(CorePlugin.getInstance().getNetworkPlayerManager().getByUuid(removingPlayer));
    }

    @Subscription(action = "GLOBAL_PLAYER_ADDITION")
    public void onGlobalPlayerAddition(JsonAppender jsonAppender) {
        final UUID uuid = UUID.fromString(jsonAppender.getParam("UUID"));
        final String name = jsonAppender.getParam("NAME");
        final String rank = jsonAppender.getParam("RANK");
        final String globalServer = jsonAppender.getParam("SERVER");
        final String syncCode = jsonAppender.getParam("SYNC_CODE");
        final boolean disallowed = Boolean.parseBoolean(jsonAppender.getParam("DISALLOWED"));

        final boolean dmsEnabled = Boolean.parseBoolean(jsonAppender.getParam("DMS_ENABLED"));
        final boolean isSynced = Boolean.parseBoolean(jsonAppender.getParam("IS_SYNCED"));

        final NetworkPlayer networkPlayer = new NetworkPlayer(uuid, "", name, globalServer, rank, syncCode, dmsEnabled, isSynced, System.currentTimeMillis(), disallowed);

        CorePlugin.getInstance().getNetworkPlayerManager().getAllNetworkProfiles().add(networkPlayer);
    }
}
