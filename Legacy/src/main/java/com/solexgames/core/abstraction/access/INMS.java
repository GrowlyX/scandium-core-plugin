package com.solexgames.core.abstraction.access;

import org.bukkit.entity.Player;

public interface INMS {

    void removeExecute(Player player);
    void addExecute(Player player);

    void updateTablist();

}