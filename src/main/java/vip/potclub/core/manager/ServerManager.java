package vip.potclub.core.manager;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;

import java.util.ArrayList;

@Getter
@Setter
public class ServerManager {

    private ArrayList<Player> vanishedPlayers;

    private ServerType network;

    private boolean chatEnabled = true;
    private long chatSlow;

    public ServerManager() {
        this.vanishedPlayers = new ArrayList<>();

        setupServerType();
        CorePlugin.getInstance().getLogger().info("[Server] Loaded server type: " + this.network.getServerName() + ".");
    }

    public void setupServerType() {
        try {
            this.network = ServerType.valueOf(CorePlugin.getInstance().getConfig().getString("server.settings.server-id"));
        } catch (IllegalArgumentException e) {
            CorePlugin.getInstance().getLogger().info("Config the plugin correctly dumbass.");
            CorePlugin.getInstance().getServer().shutdown();
        }
    }
}
