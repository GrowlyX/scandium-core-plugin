package com.solexgames.core.abstraction.nms;

import org.bukkit.entity.Player;

public interface INMSHelper {

    void removeExecute(Player player);
    void addExecute(Player player);

    void updateTablist();

}
