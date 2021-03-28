package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.Color;
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
    private List<String> staffInformation;

    private ArrayList<NetworkServer> networkServers = new ArrayList<>();
    private ArrayList<Player> vanishedPlayers = new ArrayList<>();
    private ArrayList<Player> staffModePlayers = new ArrayList<>();

    private ServerType network;
    private String automaticallyPutInto;

    private boolean chatEnabled = true;

    private boolean clearChatJoin;
    private boolean joinMessageEnabled;
    private boolean joinMessageCentered;

    private long chatSlow;

    public ServerManager() {
        this.whitelistedPlayers = CorePlugin.getInstance().getConfig().getStringList("whitelisted");

        this.betaWhitelistedPlayers = CorePlugin.getInstance().getConfig().getStringList("beta-whitelisted");

        this.joinMessage = CorePlugin.getInstance().getConfig().getStringList("player-join.join-message.message");
        this.clearChatJoin = CorePlugin.getInstance().getConfig().getBoolean("player-join.clear-chat");
        this.joinMessageEnabled = CorePlugin.getInstance().getConfig().getBoolean("player-join.join-message.enabled");
        this.joinMessageCentered = CorePlugin.getInstance().getConfig().getBoolean("player-join.join-message.centered");
        this.automaticallyPutInto = CorePlugin.getInstance().getConfig().getString("settings.automatic-string");

        this.staffInformation = Color.translate(CorePlugin.getInstance().getConfig().getStringList("staff-information"));

        setupServerType();
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

    public boolean isOnlineNetwork(String player) {
        return this.getNetworkServers().stream().filter(server -> server.getAllPlayers().contains(player)).findFirst().orElse(null) != null;
    }

    public NetworkServer getServer(String player) {
        return this.getNetworkServers().stream().filter(server -> server.getAllPlayers().contains(player)).findFirst().orElse(null);
    }

    public void setupServerType() {
        try {
            this.network = ServerType.valueOf(CorePlugin.getInstance().getConfig().getString("server.settings.server-id"));
        } catch (Exception e) {
            CorePlugin.getInstance().logConsole("&cYour Server ID is not correct! &7Please check your config and try again.");
            CorePlugin.getInstance().getServer().shutdown();
        }
    }
}
