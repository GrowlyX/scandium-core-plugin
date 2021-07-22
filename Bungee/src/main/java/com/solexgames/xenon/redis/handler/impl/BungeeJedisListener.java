package com.solexgames.xenon.redis.handler.impl;

import com.solexgames.xenon.redis.annotation.Subscription;
import com.solexgames.xenon.redis.handler.JedisHandler;
import com.solexgames.xenon.redis.json.JsonAppender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.slf4j.Logger;

import java.net.InetSocketAddress;

/**
 * @author GrowlyX
 * @since 7/20/2021
 */

public class BungeeJedisListener implements JedisHandler {

    @Subscription(action = "SERVER_ADD")
    public void onMaintenanceAdd(JsonAppender jsonAppender) {
        final InetSocketAddress socketAddress = new InetSocketAddress(jsonAppender.getParam("HOST"), Integer.parseInt(jsonAppender.getParam("PORT")));
        final ServerInfo info = ProxyServer.getInstance().constructServerInfo(jsonAppender.getParam("NAME"), socketAddress, "Xenon", false);

        ProxyServer.getInstance().getServers().put(info.getName(), info);

        System.out.println("[Global]");
    }
}
