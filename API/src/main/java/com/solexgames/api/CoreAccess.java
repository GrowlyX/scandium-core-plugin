package com.solexgames.api;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.util.Color;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public int getExperience(Player player) {
        return this.plugin.getPlayerManager().getPlayer(player).getExperience();
    }

    public List<Punishment> getPunishments(Player player) {
        return this.plugin.getPlayerManager().getPlayer(player).getPunishments();
    }

    public List<Prefix> getPrefixes(Player player) {
        return this.plugin.getPlayerManager().getPlayer(player).getAllPrefixes().stream().map(Prefix::getByName).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Grant> getGrants(Player player) {
        return this.plugin.getPlayerManager().getPlayer(player).getAllGrants();
    }

    public Prefix getPrefix(Player player) {
        return this.plugin.getPlayerManager().getPlayer(player).getAppliedPrefix();
    }

    public String getRankPrefix(Player player) {
        return Color.translate(this.plugin.getPlayerManager().getPlayer(player).getActiveGrant().getRank().getPrefix());
    }

    public String getRankSuffix(Player player) {
        return Color.translate(this.plugin.getPlayerManager().getPlayer(player).getActiveGrant().getRank().getSuffix());
    }

    public boolean isRestricted(Player player) {
        return this.plugin.getPlayerManager().getPlayer(player).isCurrentlyRestricted();
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
}
