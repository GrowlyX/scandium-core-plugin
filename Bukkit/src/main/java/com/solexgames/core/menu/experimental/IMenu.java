package com.solexgames.core.menu.experimental;

import com.solexgames.core.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface IMenu extends InventoryHolder {

    default void openMenu(Player player) {
        player.openInventory(this.getInventory());
    }

    default void onInventoryDrag(InventoryDragEvent event) {
        if (InventoryUtil.clickedTopInventory(event)) {
            event.setCancelled(true);
        }
    }

    void onInventoryClick(InventoryClickEvent event) throws IOException, ParseException;

    default void onInventoryClose(InventoryCloseEvent event) {
    }

}
