package com.solexgames.core.menu.extend.punish.history;

import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.punishment.PunishmentType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class PunishHistoryViewMainMenu extends AbstractInventoryMenu {

    private Player player;
    private String target;

    public PunishHistoryViewMainMenu(Player player, String target) {
        super("Punishment history of: " + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target), 9);
        this.player = player;
        this.target = target;
        this.update();
    }

    public void update() {
        this.inventory.setItem(2, new ItemBuilder(Material.WOOL)
                .setDisplayName("&6Warnings")
                .addLore(
                        "&7Click to all view warnings",
                        "&7in relation to " + this.target + "."
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(Material.WOOL, 4)
                .setDisplayName("&6Kicks")
                .addLore(
                        "&7Click to all view kicks",
                        "&7in relation to " + this.target + "."
                )
                .create()
        );
        this.inventory.setItem(4, new ItemBuilder(Material.WOOL, 1)
                .setDisplayName("&6Mutes")
                .addLore(
                        "&7Click to all view mutes",
                        "&7in relation to " + this.target + "."
                )
                .create()
        );
        this.inventory.setItem(5, new ItemBuilder(Material.WOOL, 14)
                .setDisplayName("&6Bans")
                .addLore(
                        "&7Click to all view bans",
                        "&7in relation to " + this.target + "."
                )
                .create()
        );
        this.inventory.setItem(6, new ItemBuilder(Material.WOOL, 15)
                .setDisplayName("&6Blacklists")
                .addLore(
                        "&7Click to all view blacklists",
                        "&7in relation to " + this.target + "."
                )
                .create()
        );
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;
            switch (event.getRawSlot()) {
                case 2:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.WARN).open(this.player);
                    break;
                case 3:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.KICK).open(this.player);
                    break;
                case 4:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.MUTE).open(this.player);
                    break;
                case 5:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.BAN).open(this.player);
                    break;
                case 6:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.BLACKLIST).open(this.player);
                    break;
            }
        }
    }
}
