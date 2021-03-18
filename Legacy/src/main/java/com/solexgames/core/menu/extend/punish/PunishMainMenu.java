package com.solexgames.core.menu.extend.punish;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.menu.extend.punish.history.PunishHistoryViewMainMenu;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

@Getter
@Setter
public class PunishMainMenu extends AbstractInventoryMenu {

    private Player player;
    private String target;

    public PunishMainMenu(Player player, String target) {
        super("Punishment menu for: " + Color.translate("&b") + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target), 9*3);
        this.player = player;
        this.target = target;
        this.update();
    }

    public void update() {
        this.inventory.setItem(12, new ItemBuilder(XMaterial.ORANGE_DYE.parseMaterial(), 14)
                .setDisplayName("&6Punish")
                .addLore(
                        "&7Start the punishment process",
                        "&7for this player.",
                        "",
                        "&eClick to start process."
                )
                .create()
        );
        this.inventory.setItem(14, new ItemBuilder(XMaterial.CYAN_DYE.parseMaterial(), 6)
                .setDisplayName("&6Punishments")
                .addLore(
                        "&7View all punishments",
                        "&7in relation to this player.",
                        "",
                        "&eClick to view all punishments."
                )
                .create()
        );
        this.inventory.setItem(13, new ItemBuilder(XMaterial.SKELETON_SKULL.parseMaterial())
                .setDurability(3)
                .setOwner(target)
                .setDisplayName(CorePlugin.getInstance().getServerManager().getNetwork().getMainColor() + ChatColor.BOLD.toString() + "Punishments")
                .addLore(
                        "&7Welcome to the punishment main",
                        "&7menu of " + target + "!"
                )
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
