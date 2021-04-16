package com.solexgames.core.util;

import lombok.experimental.UtilityClass;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

@UtilityClass
public final class InventoryUtil {

    public static ItemStack[] fixInventoryOrder(ItemStack[] source) {
        ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    public static boolean clickedTopInventory(InventoryDragEvent event) {
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();

        if (topInventory == null) {
            return false;
        }

        boolean result = false;
        int size = topInventory.getSize();

        for (Integer entry : event.getNewItems().keySet()) {
            if (entry >= size) continue;
            result = true;

            break;
        }

        return result;
    }
}
