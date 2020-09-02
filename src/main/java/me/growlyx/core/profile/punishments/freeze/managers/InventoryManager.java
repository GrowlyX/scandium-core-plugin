package me.growlyx.core.profile.punishments.freeze.managers;

import me.growlyx.core.Core;
import me.growlyx.core.profile.Manager;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager extends Manager {
    private Inventory frozenInv;

    public InventoryManager(final Core plugin) {
        super(plugin);
        this.initiateFrozenInv();
    }

    private void initiateFrozenInv() {

        this.frozenInv = this.Plugin.getServer().createInventory((InventoryHolder)null, 9, CC.translate(Messages.string("FROZEN.TITLE")));
        final ItemStack paper = new ItemStack(Material.PAPER);

        final ItemMeta itemMeta = paper.getItemMeta();
        final List<String> lores = new ArrayList<String>();

        lores.add(0, CC.translate(Messages.string("FROZEN.LINES.LINE-1")));
        lores.add(1, CC.translate(Messages.string("FROZEN.LINES.LINE-2")));

        itemMeta.setLore((List)lores);
        itemMeta.setDisplayName(CC.translate(Messages.string("FROZEN.TS-DISCORD")));
        paper.setItemMeta(itemMeta);
        this.frozenInv.setItem(4, paper);

    }

    public Inventory getFrozenInv() {
        return this.frozenInv;
    }
}
