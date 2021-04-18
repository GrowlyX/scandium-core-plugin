package com.solexgames.core.menu.experimental;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Getter
public abstract class AbstractMenu implements IMenu {

    protected final Inventory inventory;

    protected AbstractMenu() {
        this.inventory = CorePlugin.getInstance().getServer().createInventory(this, this.getSize(), Color.translate(this.getTitle()));

        this.getItems().forEach(this.inventory::setItem);
    }

    /**
     * An abstract method to get all menu items.
     * <p>
     * @return All menu buttons in a {@link HashMap<>}
     */
    public abstract HashMap<Integer, ItemStack> getItems();

    /**
     * An abstract method to get the title of the menu.
     * <p>
     * @return The menu title.
     */
    public abstract String getTitle();

    /**
     * An abstract method to get the size of the menu.
     * <p>
     * @return The menu size.
     */
    public abstract int getSize();

}
