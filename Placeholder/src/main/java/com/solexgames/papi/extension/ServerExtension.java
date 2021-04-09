package com.solexgames.papi.extension;

import com.solexgames.core.server.NetworkServer;
import com.solexgames.papi.PlaceholderPlugin;
import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * @author GrowlyX
 * @since 4/9/2021
 */

@AllArgsConstructor
public class ServerExtension extends PlaceholderExpansion {

    private final @NotNull NetworkServer networkServer;

    @Override
    public @NotNull String getIdentifier() {
        return this.networkServer.getServerName();
    }

    @Override
    public @NotNull String getAuthor() {
        return "SolexGames";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params.toLowerCase()) {
            case "long-status":
                return PlaceholderPlugin.getInstance().getExtensionManager().getFancyStatus(this.networkServer);
            case "status":
                return PlaceholderPlugin.getInstance().getExtensionManager().getShortStatus(this.networkServer);
            case "joinable":
                return PlaceholderPlugin.getInstance().getExtensionManager().getJoinableStatus(this.networkServer);
            default:
                return null;
        }
    }
}
