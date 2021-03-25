package com.solexgames.core.menu.experimental;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

import java.util.HashMap;

public class DevMenu extends AbstractMenu {

    @Override
    public HashMap<Integer, ItemStack> getItems() {
        HashMap<Integer, ItemStack> hashMap = new HashMap<>();

        hashMap.put(1, new ItemBuilder(XMaterial.RED_DYE.parseMaterial()).create());

        return hashMap;
    }

    @Override
    public String getTitle() {
        return "horse";
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {

    }
}
