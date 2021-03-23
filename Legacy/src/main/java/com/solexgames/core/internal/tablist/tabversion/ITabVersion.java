package com.solexgames.core.internal.tablist.tabversion;

import org.bukkit.entity.Player;

public interface ITabVersion {

    void setup(Player player);
    void addPlayerInfo(Player player, Object ep);
    void removePlayerInfo(Player player, Object ep);
    void update(Player player);
    int getSlots(Player player);
    Object createPlayer(Player player, String name);
    void setHeaderAndFooter(Player player);
    void removePlayerInfoForEveryone(Player player);
    void addAllOnlinePlayers(Player player);
    void removeAllOnlinePlayers(Player player);
}
