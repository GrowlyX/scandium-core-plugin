package com.solexgames.core.chat.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.chat.IChatFormat;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DefaultChatFormat implements IChatFormat {

    @Override
    public String getFormatted(Player sender, Player receiver, String message) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(sender);
        final Rank activeRank = potPlayer.getActiveGrant().getRank();

        final String defaultFormat = Color.translate(CorePlugin.getInstance().getServerSettings().getChatFormat()
                .replace("<prefix>", (potPlayer.getAppliedPrefix() != null ? " " + potPlayer.getAppliedPrefix().getPrefix() : ""))
                .replace("<rank_prefix>", (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getPrefix() : activeRank.getPrefix()))
                .replace("<rank_suffix>", (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getSuffix() : activeRank.getSuffix()))
                .replace("<rank_color>", (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getColor() : activeRank.getColor()))
                .replace("<custom_color>", (potPlayer.getCustomColor() != null ? potPlayer.getCustomColor().toString() : ""))
                .replace(receiver.getName(), ChatColor.YELLOW + receiver.getName() + ChatColor.WHITE)
                .replace("<player_name>", activeRank.getItalic() + sender.getName())
        );

        return defaultFormat.replace("<message>", message);
    }
}
