package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ServerManager {

    private final long heartbeatCheckTimeout = 15_000L;

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

    private Location spawnLocation;

    private long chatSlow;

    public ServerManager() {
        final CorePlugin plugin = CorePlugin.getInstance();

        this.joinMessage = plugin.getConfig().getStringList("player-join.join-message.message");
        this.clearChatJoin = plugin.getConfig().getBoolean("player-join.clear-chat");
        this.joinMessageEnabled = plugin.getConfig().getBoolean("player-join.join-message.enabled");
        this.joinMessageCentered = plugin.getConfig().getBoolean("player-join.join-message.centered");
        this.automaticallyPutInto = plugin.getConfig().getString("settings.automatic-string");

        this.staffInformation = Color.translate(plugin.getConfig().getStringList("staff-information"));

        this.spawnLocation = CorePlugin.GSON.fromJson(plugin.getConfig().getString("locations.spawnpoint"), Location.class);

        this.setupServerType();
    }

    public void removeNetworkServer(NetworkServer networkServer) {
        this.networkServers.remove(networkServer);
    }

    public void addNetworkServer(NetworkServer networkServer) {
        this.networkServers.add(networkServer);
    }

    public boolean existServer(String networkServer) {
        return this.networkServers.contains(NetworkServer.getByName(networkServer));
    }

    public boolean isOnlineNetwork(String player) {
        return this.getNetworkServers().stream().filter(server -> server.getAllPlayers().contains(player)).findFirst().orElse(null) != null;
    }

    public NetworkServer getServer(String player) {
        return this.getNetworkServers().stream()
                .filter(server -> server.getAllPlayers().contains(player))
                .findFirst()
                .orElse(null);
    }

    public void setupServerType() {
        try {
            this.network = ServerType.valueOf(CorePlugin.getInstance().getConfig().getString("server.settings.server-id"));
        } catch (Exception ignored) {
            CorePlugin.getInstance().logConsole("&cYour Server ID is not correct! &7Please check your config and try again.");
            CorePlugin.getInstance().getServer().shutdown();
        }
    }

    public void syncPermissions(Player player, List<String> permissions) {
        if (!permissions.isEmpty()) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            try {
                dataOutputStream.writeUTF("core-permissions");
                dataOutputStream.writeUTF(player.getName());
                dataOutputStream.writeUTF(String.join(":", permissions));
            } catch (Exception exception) {
                System.out.println("[Messenger] Failed to sync permissions: " + exception.getMessage());
            }

            player.sendPluginMessage(CorePlugin.getInstance(), "core-permissions", byteArrayOutputStream.toByteArray());
        }
    }
}
