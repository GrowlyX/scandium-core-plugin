package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.manager.ServerManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerHeartbeatTask extends BukkitRunnable {

    private final long TIME_OUT_DELAY = 15_000L;

    @Override
    public void run() {
        final CorePlugin plugin = CorePlugin.getInstance();
        final ServerManager serverManager = plugin.getServerManager();

        serverManager.getNetworkServers().stream()
                .filter(server -> server != null && (System.currentTimeMillis() - server.getLastUpdate()) > this.TIME_OUT_DELAY)
                .forEach(server -> {
                    plugin.getServerManager().getNetworkServers().remove(server);
                    plugin.logConsole("&cThe server with the name &4'" + server.getServerName() + "'&c has been removed as it's last update was longer than &415 seconds&c ago.");
                });
    }
}
