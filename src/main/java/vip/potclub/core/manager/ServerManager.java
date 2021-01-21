package vip.potclub.core.manager;

import lombok.Getter;
import lombok.Setter;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;

@Getter
@Setter
public class ServerManager {

    public ServerType network;

    public ServerManager() {
        setupServerType();
    }

    public void setupServerType() {
        try {
            this.network = ServerType.valueOf(CorePlugin.getInstance().getConfig().getString("server.settings.server-id"));
        } catch (IllegalArgumentException e) {
            CorePlugin.getInstance().getLogger().info("Could not find a valid server.");
            CorePlugin.getInstance().getServer().shutdown();
        }
    }
}
