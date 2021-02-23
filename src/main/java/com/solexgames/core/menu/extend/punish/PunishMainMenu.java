package com.solexgames.core.menu.extend.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.menu.InventoryMenuItem;
import com.solexgames.core.menu.extend.punish.history.PunishHistoryViewMainMenu;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

@Getter
@Setter
public class PunishMainMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private String target;

    public PunishMainMenu(Player player, String target) {
        super("Punishment - Main", 9*3);
        this.player = player;
        this.target = target;
        this.update();
    }

    private void update() {

        ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
        playerheadmeta.setOwner(target);
        playerheadmeta.setLore(Arrays.asList(
                Color.translate("&7  "),
                Color.translate("&7Welcome to the punishment"),
                Color.translate("&7menu of " + target + "!"),
                Color.translate("&7  ")
        ));
        playerheadmeta.setDisplayName(Color.translate("&aPunishments"));
        playerhead.setItemMeta(playerheadmeta);

        this.inventory.setItem(12, new InventoryMenuItem(Material.INK_SACK, 14)
                .setDisplayName("&6Punish")
                .addLore(
                        "",
                        "&7Start the punishment process",
                        "&7for this player.",
                        "",
                        "&eClick to start process."
                )
                .create()
        );

        this.inventory.setItem(14, new InventoryMenuItem(Material.INK_SACK, 6)
                .setDisplayName("&6Punishments")
                .addLore(
                        "",
                        "&7View all punishments",
                        "&7in relation to this player.",
                        "",
                        "&eClick to view all punishments."
                )
                .create()
        );

        this.inventory.setItem(13, playerhead);
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

            if (item == null || item.getType() == Material.AIR) return;
            switch (event.getRawSlot()) {
                case 12:
                    new PunishSelectPunishTypeMenu(player, target).open(player);
                    break;
                case 14:
                    new PunishHistoryViewMainMenu(player, target).open(player);
                    break;
            }
        }
    }
}
