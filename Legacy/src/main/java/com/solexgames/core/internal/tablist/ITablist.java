package com.solexgames.core.internal.tablist;

import org.bukkit.entity.Player;

import java.util.List;

public interface ITablist {

    List<TablistElement> getElements(Player player);
    String getHeader(Player player);
    String getFooter(Player player);
}
