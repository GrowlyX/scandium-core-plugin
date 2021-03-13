package com.solexgames.core.menu;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

@Getter
public abstract class AbstractInventoryMenu implements IMenu {

    protected Inventory inventory;

    protected AbstractInventoryMenu(String title, int size) {
        this.inventory = CorePlugin.getInstance().getServer().createInventory(this, size, Color.translate(title));
    }
}
