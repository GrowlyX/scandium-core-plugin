package com.solexgames.core.nms;

import org.bukkit.entity.Player;

public interface INMSHelper {

    void removeExecute(Player player);
    void addExecute(Player player);

    void updateTablist();

}
