package com.solexgames.core.serializer.impl;

import com.solexgames.core.serializer.DataSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * @author GrowlyX
 * @since 4/20/2021
 */

public class ItemStackSerializer extends DataSerializer<ItemStack> {

    @Override
    public Class<ItemStack> getClazz() {
        return ItemStack.class;
    }
}
