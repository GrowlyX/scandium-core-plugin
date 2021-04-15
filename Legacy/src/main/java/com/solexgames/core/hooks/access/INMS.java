package com.solexgames.core.hooks.access;

import org.bukkit.entity.Player;

public interface INMS {

    void removeExecute(Player player);
    void addExecute(Player player);

    void updateTablist();
    void setupTablist(Player player);

}
