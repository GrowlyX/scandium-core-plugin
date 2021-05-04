package com.solexgames.core.chat.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.chat.IChatFormat;
import com.solexgames.core.util.Color;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PAPIChatFormat implements IChatFormat {

    @Override
    public String getFormatted(Player sender, Player receiver, String message) {
        return PlaceholderAPI.setPlaceholders(sender, Color.translate(CorePlugin.getInstance().getServerSettings().getChatFormat()));
    }
}
