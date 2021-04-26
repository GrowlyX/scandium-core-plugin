package com.solexgames.core.hooks.client;

import org.bukkit.entity.Player;

import java.util.Set;

public interface IClient {

    void enableStaffModules(Player player);
    void disableStaffModules(Player player);
    void sendNotification(Player player, String translate);
    Set<Player> getOnlineLunarPlayers();

}
