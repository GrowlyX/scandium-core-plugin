package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.server.NetworkServer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ServerManager {

    private List<String> whitelistedPlayers;
    private List<String> betaWhitelistedPlayers;
    private List<String> joinMessage;

    private ArrayList<NetworkServer> networkServers = new ArrayList<>();
    private ArrayList<Player> vanishedPlayers = new ArrayList<>();
    private ArrayList<Player> staffModePlayers = new ArrayList<>();

    private ServerType network;

    private boolean chatEnabled = true;

    private boolean clearChatJoin;
    private boolean joinMessageEnabled;
    private boolean joinMessageCentered;

    private long chatSlow;

    public ServerManager() {
        this.whitelistedPlayers = CorePlugin.getInstance().getWhitelistConfig().getStringList("whitelisted");

        this.betaWhitelistedPlayers = CorePlugin.getInstance().getWhitelistConfig().getStringList("beta-whitelisted");
        this.betaWhitelistedPlayers.forEach(s -> this.whitelistedPlayers.add(s));

        this.joinMessage = CorePlugin.getInstance().getConfig().getStringList("player-join.join-message.message");
        this.clearChatJoin = CorePlugin.getInstance().getConfig().getBoolean("player-join.clear-chat");
        this.joinMessageEnabled = CorePlugin.getInstance().getConfig().getBoolean("player-join.join-message.enabled");
        this.joinMessageCentered = CorePlugin.getInstance().getConfig().getBoolean("player-join.join-message.centered");

        setupServerType();
        CorePlugin.getInstance().getLogger().info("[Network] Loaded server type: " + this.network.getServerName() + ".");
    }

    public void removeNetworkServer(NetworkServer networkServer) {
        networkServers.remove(networkServer);
    }

    public void addNetworkServer(NetworkServer networkServer) {
        networkServers.add(networkServer);
    }

    public boolean existServer(String networkServer) {
        return networkServers.contains(NetworkServer.getByName(networkServer));
    }

    public void setupServerType() {
        try {
            this.network = ServerType.valueOf(CorePlugin.getInstance().getConfig().getString("server.settings.server-id"));
        } catch (IllegalArgumentException e) {
            CorePlugin.getInstance().getLogger().info("Please double check your configuration! The server ID is not correct.");
            CorePlugin.getInstance().getServer().shutdown();
        }
    }
}
