package com.solexgames.papi.listener;

import com.solexgames.core.listener.custom.ServerRetrieveEvent;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.papi.PlaceholderPlugin;
import com.solexgames.papi.extension.ServerExtension;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author GrowlyX
 * @since 4/9/2021
 */

public class ServerListener implements Listener {

    @EventHandler
    public void onRetrieve(ServerRetrieveEvent event) {
        NetworkServer server = event.getServer();

        if (server != null) {
            if (!server.getServerName().toLowerCase().contains("event")) {
                ServerExtension extension = new ServerExtension(server);

                if (extension.canRegister()) {
                    extension.register();
                }

                PlaceholderPlugin.getInstance().getExtensionManager().addPlaceholder(server, extension);
            }
        }
    }
}
