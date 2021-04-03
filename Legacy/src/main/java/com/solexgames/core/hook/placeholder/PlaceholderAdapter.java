package com.solexgames.core.hook.placeholder;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAdapter extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "scandium";
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
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getPlayer());

        if (potPlayer == null) {
            return null;
        }

        switch (params.toLowerCase()) {
            case "rankcolor":
                if (potPlayer.getActiveGrant().getRank().getColor() != null) {
                    return potPlayer.getActiveGrant().getRank().getColor();
                } else {
                    return "&7";
                }
            case "rankname":
                if (potPlayer.getActiveGrant().getRank().getName() != null) {
                    return potPlayer.getActiveGrant().getRank().getName();
                } else {
                    return "Default";
                }
            case "rankprefix":
                if (potPlayer.getActiveGrant().getRank().getPrefix() != null) {
                    return potPlayer.getActiveGrant().getRank().getPrefix();
                } else {
                    return "&7";
                }
            case "ranksuffix":
                if (potPlayer.getActiveGrant().getRank().getSuffix() != null) {
                    return potPlayer.getActiveGrant().getRank().getSuffix();
                } else {
                    return "&7";
                }
            case "experience":
                return String.valueOf(potPlayer.getExperience());
            case "tag":
                if (potPlayer.getAppliedPrefix() != null) {
                    return potPlayer.getAppliedPrefix().getPrefix();
                } else {
                    return "&7";
                }
            case "customcolor":
                if (potPlayer.getCustomColor() != null) {
                    return potPlayer.getCustomColor().toString();
                } else {
                    return "&7";
                }
            case "discord":
                if (potPlayer.getSyncDiscord() != null) {
                    return potPlayer.getSyncDiscord();
                } else {
                    return "&cNot synced.";
                }
            case "language":
                if (potPlayer.getLanguage() != null) {
                    return potPlayer.getLanguage().getLanguageName();
                } else {
                    return "English";
                }
        }

        return null;
    }
}
