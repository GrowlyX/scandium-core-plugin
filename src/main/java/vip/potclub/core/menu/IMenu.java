package vip.potclub.core.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.json.simple.parser.ParseException;
import vip.potclub.core.util.InventoryUtil;

import java.io.IOException;

public interface IMenu extends InventoryHolder {

    default void open(Player player) {
        player.openInventory(this.getInventory());
    }

    void onInventoryClick(InventoryClickEvent event) throws IOException, ParseException;

    default void onInventoryDrag(InventoryDragEvent event) {
        if (InventoryUtil.clickedTopInventory(event)) event.setCancelled(true);
    }

    default void onInventoryClose(InventoryCloseEvent event) { }
}
