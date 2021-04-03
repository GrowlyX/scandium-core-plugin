package com.solexgames.api;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.global.NetworkPlayer;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 4/2/2021
 */

public class CoreAccess {

    private final CorePlugin plugin;

    public CoreAccess() {
        this.plugin = CorePlugin.getInstance();
    }

    public ServerManager getServerManager() {
        return this.plugin.getServerManager();
    }

    public PlayerManager getPlayerManager() {
        return this.plugin.getPlayerManager();
    }

    public PotPlayer fetchProfile(Player player) {
        return this.plugin.getPlayerManager().getPlayer(player);
    }

    public NetworkPlayer fetchGlobalProfile(Player player) {
        return this.plugin.getPlayerManager().getNetworkPlayer(player);
    }

    public PotPlayer fetchProfile(String player) {
        return this.plugin.getPlayerManager().getPlayer(player);
    }

    public NetworkPlayer fetchGlobalProfile(String player) {
        return this.plugin.getPlayerManager().getNetworkPlayer(player);
    }

    public PotPlayer fetchProfile(String player) {
        return this.plugin.getPlayerManager().getPlayer(player);
    }

    public NetworkPlayer getInstance(String player) {
        return this.plugin.getPlayerManager().getNetworkPlayer(player);
    }
}
