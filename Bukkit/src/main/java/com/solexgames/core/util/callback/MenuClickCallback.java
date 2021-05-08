package com.solexgames.core.util.callback;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface MenuClickCallback {

    void call(Player player, ClickType clickType);

}
