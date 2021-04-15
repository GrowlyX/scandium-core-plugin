package com.solexgames.core.hooks.client;

import org.bukkit.entity.Player;

import java.util.Set;

public abstract class AbstractClientHook {

    public abstract void enableStaffModules(Player player);
    public abstract void disableStaffModules(Player player);
    public abstract void sendNotification(Player player, String translate);
    public abstract Set<Player> getOnlineLunarPlayers();

}
