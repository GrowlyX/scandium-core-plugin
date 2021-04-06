package com.solexgames.core.menu.impl.punish.history;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.external.pagination.impl.PunishViewPaginatedMenu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
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
        this.inventory.setItem(2, new ItemBuilder(XMaterial.WHITE_WOOL.parseMaterial())
                .setDisplayName("&6Warnings")
                .addLore(
                        "&7Click to all view warnings",
                        "&7in relation to " + this.target + "."
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(XMaterial.YELLOW_WOOL.parseMaterial(), 4)
                .setDisplayName("&6Kicks")
                .addLore(
                        "&7Click to all view kicks",
                        "&7in relation to " + this.target + "."
                )
                .create()
        );
        this.inventory.setItem(4, new ItemBuilder(XMaterial.ORANGE_WOOL.parseMaterial(), 1)
                .setDisplayName("&6Mutes")
                .addLore(
                        "&7Click to all view mutes",
                        "&7in relation to " + this.target + "."
                )
                .create()
        );
        this.inventory.setItem(5, new ItemBuilder(XMaterial.RED_WOOL.parseMaterial(), 14)
                .setDisplayName("&6Bans")
                .addLore(
                        "&7Click to all view bans",
                        "&7in relation to " + this.target + "."
                )
                .create()
        );
        this.inventory.setItem(6, new ItemBuilder(XMaterial.BLACK_WOOL.parseMaterial(), 15)
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

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            switch (event.getRawSlot()) {
                case 2:
                    new PunishViewPaginatedMenu(player, this.target, PunishmentType.WARN).openMenu(this.player);
                    break;
                case 3:
                    new PunishViewPaginatedMenu(player, this.target, PunishmentType.KICK).openMenu(this.player);
                    break;
                case 4:
                    new PunishViewPaginatedMenu(player, this.target, PunishmentType.MUTE).openMenu(this.player);
                    break;
                case 5:
                    new PunishViewPaginatedMenu(player, this.target, PunishmentType.BAN).openMenu(this.player);
                    break;
                case 6:
                    new PunishViewPaginatedMenu(player, this.target, PunishmentType.BLACKLIST).openMenu(this.player);
                    break;
            }
        }
    }
}
