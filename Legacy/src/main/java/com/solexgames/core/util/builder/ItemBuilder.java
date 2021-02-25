package com.solexgames.core.util.builder;

import com.solexgames.core.util.Color;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int data) {
        this.itemStack = new ItemStack(material, 1, (short) data);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public SkullMeta getSkullMeta() {
        if (this.itemStack.getType().equals(Material.SKULL_ITEM)) {
            return (SkullMeta) this.itemMeta;
        } else {
            return null;
        }
    }

    public ItemBuilder setOwner(String name) {
        if (this.itemStack.getType().equals(Material.SKULL_ITEM)) {
            this.getSkullMeta().setOwner(name);
        }
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        itemMeta.setDisplayName(Color.translate(name));
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        itemStack.setDurability((short) durability);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        Object object = itemMeta.getLore();
        if (object == null) object = new ArrayList<>();

        ((List) object).add(Color.translate(lore));
        itemMeta.setLore((List<String>) object);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        itemMeta.setLore(Color.translate(lore));
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        List<String> strings = new ArrayList<>();
        for (String string : lore) {
            strings.add(Color.translate(string));
        }
        itemMeta.setLore(strings);
        return this;
    }

    public ItemBuilder setEnchant(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setColor(org.bukkit.Color color) {
        if (itemStack.getType() != null && itemStack.getType().name().contains("LEATHER")) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) itemMeta;
            armorMeta.setColor(color);
        }
        return this;
    }

    public ItemStack create() {
        if (itemMeta != null) {
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
