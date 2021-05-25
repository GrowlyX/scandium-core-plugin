package com.solexgames.core.chat;

import com.solexgames.core.player.PotPlayer;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author GrowlyX
 * @since 5/25/2021
 */

public interface IChatCheck {

    void check(AsyncPlayerChatEvent event, PotPlayer potPlayer);

}
