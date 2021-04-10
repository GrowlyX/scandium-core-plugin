package com.solexgames.velocity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.solexgames.velocity.listener.PlayerListener;
import com.solexgames.velocity.proxy.ProxyManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @author GrowlyX
 * @since 3/5/2021
 * @version 1.0
 *
 * @see ProxyServer
 * <p>
 * Holds instances to everything Xenon-velocity related.
 */

@Getter
@Plugin(id = "xenon-velocity", name = "Xenon", version = "1.0-${git.commit.id.abbrev}", url = "https://solexgames.com", authors = {"SolexGames"})
public class CorePlugin {

    @Getter
    private static CorePlugin instance;

    private final ProxyServer server;
    private final Logger logger;

    private final ArrayList<String> whitelistedPlayers = new ArrayList<>();
    private final ArrayList<ServerInfo> hubServers = new ArrayList<>();

    public static Gson GSON;
    public static GsonBuilder GSONBUILDER;

/*    private Configuration configuration;
    private Configuration redisConfig;*/
    private File configurationFile;

    private ProxyManager proxyManager;

    private boolean maintenance;

    private String maintenanceMotd;
    private String normalMotd;
    private String maintenanceMessage;

    private Executor redisExecutor;

    @Inject
    public CorePlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;

        this.server.getAllServers().stream()
                .map(RegisteredServer::getServerInfo)
                .filter(serverInfo -> (serverInfo.getName().contains("hub") || serverInfo.getName().contains("Hub") || serverInfo.getName().contains("Lobby") || serverInfo.getName().contains("lobby")) && !(serverInfo.getName().contains("Restricted") || serverInfo.getName().contains("restricted")))
                .forEach(this.hubServers::add);

        server.getEventManager().register(this, new PlayerListener());
    }

    public RegisteredServer getBestHub() {
        return this.hubServers.stream()
                .filter(Objects::nonNull)
                .map(serverInfo -> this.server.getServer(serverInfo.getName()).orElse(null))
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(server -> (int) + (long) server.getPlayersConnected().size()))
                .orElse(null);
    }
}
