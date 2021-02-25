package com.solexgames.core.menu;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

@Getter
public abstract class AbstractInventoryMenu implements IMenu {

    protected Inventory inventory;

    public AbstractInventoryMenu(String title, int size) {
        this.inventory = CorePlugin.getInstance().getServer().createInventory(this, size, title.length() > 32 ? title.substring(0, 32) : Color.translate(title));
    }
}
