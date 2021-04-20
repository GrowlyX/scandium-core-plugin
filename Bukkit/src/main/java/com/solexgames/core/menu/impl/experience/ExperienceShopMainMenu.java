package com.solexgames.core.menu.impl.experience;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.menu.impl.experience.buy.PrefixPurchaseMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ExperienceShopMainMenu extends AbstractInventoryMenu {

    public ExperienceShopMainMenu() {
        super("Purchase", 9);
        this.update();
    }

    @Override
    public void update() {
        this.inventory.setItem(3, new ItemBuilder(XMaterial.NAME_TAG.parseMaterial())
                .setDisplayName("&3&lTags")
                .addLore(ChatColor.GRAY + "Click to view all purchasable tags!")
                .create());
        this.inventory.setItem(5, new ItemBuilder(XMaterial.BLAZE_POWDER.parseMaterial())
                .setDisplayName("&6&lRanks")
                .addLore(ChatColor.GRAY + "Click to view all purchasable ranks!")
                .create());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();

        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            if (event.getRawSlot() == 3) {
                new PrefixPurchaseMenu().openMenu(player);
            }
            if (event.getRawSlot() == 6) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "I'm sorry, but this shop is currently disabled.");
            }
        }
    }
}
