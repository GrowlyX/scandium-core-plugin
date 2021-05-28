package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.chat.IChatFormat;
import com.solexgames.core.chat.impl.DefaultChatFormat;
import com.solexgames.core.chat.impl.PAPIChatFormat;
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

    private final List<NetworkServer> networkServers = new ArrayList<>();
    private final List<Player> vanishedPlayers = new ArrayList<>();
    private final List<Player> staffModePlayers = new ArrayList<>();

    private final List<String> joinMessage;
    private final List<String> staffInformation;
    private final List<String> blockedCommands;

    private IChatFormat chatFormat;
    private ServerType network;
    private String automaticallyPutInto;
    private String commandCallback;

    private boolean chatEnabled = true;

    private boolean clearChatJoin;
    private boolean joinMessageEnabled;
    private boolean joinMessageCentered;
    private boolean joinStaffEnabled;

    private long chatSlow;

    public ServerManager() {
        final CorePlugin plugin = CorePlugin.getInstance();

        this.joinMessage = plugin.getConfig().getStringList("on-join.join-message.message");
        this.clearChatJoin = plugin.getConfig().getBoolean("on-join.clear-chat");
        this.joinMessageEnabled = plugin.getConfig().getBoolean("on-join.join-message.enabled");
        this.joinMessageCentered = plugin.getConfig().getBoolean("on-join.join-message.centered");
        this.automaticallyPutInto = plugin.getConfig().getString("language.automatic-string");

        this.blockedCommands = plugin.getConfig().getStringList("command-block.affected");
        this.commandCallback = plugin.getConfig().getString("command-block.callback");

        this.chatFormat = plugin.getConfig().getString("chat.type")
                .equals("PAPI") ? new PAPIChatFormat() : new DefaultChatFormat();

        this.joinStaffEnabled = plugin.getConfig().getBoolean("staff.join-information.enabled");
        this.staffInformation = Color.translate(plugin.getConfig().getStringList("staff.staff-information.message"));

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
            this.network = ServerType.valueOf(CorePlugin.getInstance().getConfig().getString("settings.network-id"));
        } catch (Exception ignored) {
            CorePlugin.getInstance().logConsole("&7I'm sorry, but I couldn't identify your server id.");
            CorePlugin.getInstance().logConsole("&f&oPlease double check your config or message &eSolexGames &7&omanagement.");

            CorePlugin.getInstance().getServer().shutdown();
        }
    }

    public void syncPermissions(Player player, List<String> permissions) {
        if (!permissions.isEmpty()) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            try {
                dataOutputStream.writeUTF("core:permissions");
                dataOutputStream.writeUTF(player.getName());
                dataOutputStream.writeUTF(String.join(":", permissions));

                player.sendPluginMessage(CorePlugin.getInstance(), "core:permissions", byteArrayOutputStream.toByteArray());
            } catch (Exception exception) {
                System.out.println("[Messenger] Failed to sync permissions: " + exception.getMessage());
            }
        }
    }
}
