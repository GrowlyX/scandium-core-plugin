package vip.potclub.core.menu;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import vip.potclub.core.util.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryMenuItem {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    public InventoryMenuItem(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public InventoryMenuItem(Material material, int data) {
        this.itemStack = new ItemStack(material, 1, (short) data);
        this.itemMeta = itemStack.getItemMeta();
    }

    public InventoryMenuItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public InventoryMenuItem setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public InventoryMenuItem setDisplayName(String name) {
        itemMeta.setDisplayName(Color.translate(name));
        return this;
    }

    public InventoryMenuItem addItemFlags(ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            this.itemMeta.addItemFlags(itemFlag);
        }
        return this;
    }

    public InventoryMenuItem setDurability(int durability) {
        itemStack.setDurability((short) durability);
        return this;
    }

    public InventoryMenuItem addLore(String lore) {
        Object object = itemMeta.getLore();
        if (object == null) object = new ArrayList<>();

        ((List) object).add(Color.translate(lore));
        itemMeta.setLore((List<String>) object);
        return this;
    }

    public InventoryMenuItem addLore(List<String> lore) {
        itemMeta.setLore(Color.translate(lore));
        return this;
    }

    public InventoryMenuItem addLore(String... lore) {
        List<String> strings = new ArrayList<>();
        for (String string : lore) {
            strings.add(Color.translate(string));
        }
        itemMeta.setLore(strings);
        return this;
    }

    public InventoryMenuItem setEnchant(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public InventoryMenuItem setUnbreakable(boolean unbreakable) {
        itemMeta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    public InventoryMenuItem setColor(org.bukkit.Color color) {
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
