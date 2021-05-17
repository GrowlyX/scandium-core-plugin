package com.solexgames.core.menu;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.InventoryUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.json.simple.parser.ParseException;

import java.io.IOException;

@Getter
public abstract class AbstractInventoryMenu implements InventoryHolder {

    protected Inventory inventory;

    protected AbstractInventoryMenu(String title, int size) {
        this.inventory = CorePlugin.getInstance().getServer().createInventory(this, size, Color.translate(title));
    }

    public abstract void update();

    public abstract void onInventoryClick(InventoryClickEvent event);

    public void onInventoryDrag(InventoryDragEvent event) {
        if (InventoryUtil.clickedTopInventory(event)) {
            event.setCancelled(true);
        }
    }

    public void onInventoryClose(InventoryCloseEvent event) { }

    public void open(Player player) {
        player.openInventory(this.getInventory());
    }
}
