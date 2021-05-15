package com.solexgames.core.menu.impl.punish.history;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.external.impl.PunishViewPaginatedMenu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@Setter
public class PunishHistoryViewMainMenu extends AbstractInventoryMenu {

    private Player player;
    private String target;
    private UUID targetUuid;

    public PunishHistoryViewMainMenu(Player player, UUID targetUuid, String target) {
        super("Punishments for: " + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target), 9);

        this.player = player;
        this.target = target;
        this.targetUuid = targetUuid;


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

            final ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) {
                return;
            }

            final PunishmentType punishmentType = this.getByInt(event.getRawSlot());

            if (punishmentType != null) {
                new PunishViewPaginatedMenu(this.player, this.target, this.targetUuid, punishmentType).openMenu(this.player);
            }
        }
    }

    public PunishmentType getByInt(int slot) {
        switch (slot) {
            case 2:
                return PunishmentType.WARN;
            case 4:
                return PunishmentType.MUTE;
            case 5:
                return PunishmentType.BAN;
            case 6:
                return PunishmentType.BLACKLIST;
            default:
                return PunishmentType.KICK;
        }
    }
}
