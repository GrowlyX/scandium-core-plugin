package com.solexgames.core.chat;

import org.bukkit.entity.Player;

public interface IChatFormat {

    String getFormatted(Player sender, Player receiver, String message);

}
