package com.solexgames.core.util.builder;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    private Map<Enchantment, Integer> enchantments = new HashMap<>();

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

    public ItemBuilder setOwner(String name) {
        SkullMeta skullMeta = (SkullMeta) this.itemMeta;

        if (this.itemStack.getType().equals(XMaterial.SKELETON_SKULL.parseMaterial())) {

            skullMeta.setOwner(name);
        } else {
            return this;
        }

        this.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        itemMeta.setDisplayName(Color.translate(name));
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        this.itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        itemStack.setDurability((short) durability);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        List<String> list = itemMeta.getLore();
        if (list == null) list = new ArrayList<>();

        list.add(Color.translate(lore));

        itemMeta.setLore(list);
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
