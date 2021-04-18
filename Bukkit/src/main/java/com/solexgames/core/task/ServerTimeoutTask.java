package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.listener.custom.ServerDeleteEvent;
import com.solexgames.core.listener.custom.ServerRetrieveEvent;
import com.solexgames.core.manager.ServerManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class ServerTimeoutTask extends BukkitRunnable {

    private final long TIME_OUT_DELAY = 15_000L;

    private final CorePlugin plugin;

    public ServerTimeoutTask() {
        this.plugin = CorePlugin.getInstance();
        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        try {
            this.plugin.getServerManager().getNetworkServers().stream()
                    .filter(server -> server != null && !server.getServerName().equals(CorePlugin.getInstance().getServerName()) && (System.currentTimeMillis() - server.getLastUpdate()) > this.TIME_OUT_DELAY)
                    .forEach(server -> {
                        this.plugin.getServerManager().getNetworkServers().remove(server);
                        this.plugin.logConsole("&cThe server with the name &4'" + server.getServerName() + "'&c has been removed as it's last update was longer than &415 seconds&c ago.");

                        ServerDeleteEvent deleteEvent = new ServerDeleteEvent(server);
                        CorePlugin.getInstance().getServer().getPluginManager().callEvent(deleteEvent);
                    });
        } catch (Exception ignored) {
        }
    }
}
