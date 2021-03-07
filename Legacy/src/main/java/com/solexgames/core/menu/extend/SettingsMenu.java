package com.solexgames.core.menu.extend;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class SettingsMenu extends AbstractInventoryMenu {

    private Player player;

    public SettingsMenu(Player player) {
        super("Settings", 9);
        this.player = player;
        this.update();
    }

    public void update() {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);
        this.inventory.setItem(2, new ItemBuilder(XMaterial.PAPER.parseMaterial())
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .setDisplayName("&3Global Chat")
                .addLore(
                        "",
                        "&7Would you like to be",
                        "&7able to view global",
                        "&7chat?",
                        " ",
                        "" + (potPlayer.isCanSeeGlobalChat() ? "&a&l■ " : "&8&l■ ") + "&fEnabled",
                        "" + (!potPlayer.isCanSeeGlobalChat() ? "&a&l■ " : "&8&l■ ") + "&fDisabled"
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE.parseMaterial())
                .setDisplayName("&3Server Tips")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addLore(
                        "",
                        "&7Would you like to be",
                        "&7able to view server",
                        "&7tips?",
                        " ",
                        "" + (potPlayer.isCanSeeTips() ? "&a&l■ " : "&8&l■ ") + "&fEnabled",
                        "" + (!potPlayer.isCanSeeTips() ? "&a&l■ " : "&8&l■ ") + "&fDisabled"
                )
                .create()
        );
        this.inventory.setItem(4, new ItemBuilder(XMaterial.EMERALD.parseMaterial())
                .setDisplayName("&3Receive DMs")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addLore(
                        "",
                        "&7Would you like to be",
                        "&7able to receive player",
                        "&7dms?",
                        " ",
                        "" + (potPlayer.isCanReceiveDms() ? "&a&l■ " : "&8&l■ ") + "&fEnabled",
                        "" + (!potPlayer.isCanReceiveDms() ? "&a&l■ " : "&8&l■ ") + "&fDisabled"
                )
                .create()
        );
        this.inventory.setItem(5, new ItemBuilder(XMaterial.JUKEBOX.parseMaterial())
                .setDisplayName("&3DMs Sounds")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addLore(
                        "",
                        "&7Would you like to be",
                        "&7able to receive dm",
                        "&7sounds?",
                        " ",
                        "" + (potPlayer.isCanReceiveDmsSounds() ? "&a&l■ " : "&8&l■ ") + "&fEnabled",
                        "" + (!potPlayer.isCanReceiveDmsSounds() ? "&a&l■ " : "&8&l■ ") + "&fDisabled"
                )
                .create()
        );
        if (player.hasPermission("scandium.staff")) {
            this.inventory.setItem(6, new ItemBuilder(XMaterial.BLAZE_POWDER.parseMaterial())
                    .setDisplayName("&3Staff Messages")
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                    .addLore(
                            "",
                            "&7Would you like to be",
                            "&7able to receive staff",
                            "&7messages?",
                            " ",
                            "" + (potPlayer.isCanSeeStaffMessages() ? "&a&l■ " : "&8&l■ ") + "&fEnabled",
                            "" + (!potPlayer.isCanSeeStaffMessages() ? "&a&l■ " : "&8&l■ ") + "&fDisabled"
                    )
                    .create()
            );
        } else {
            this.inventory.setItem(6, new ItemBuilder(XMaterial.BLAZE_POWDER.parseMaterial())
                    .setDisplayName("&3Global Broadcasts")
                    .addLore(
                            "",
                            "&7Would you like to be",
                            "&7able to receive global",
                            "&7broadcasts?",
                            " ",
                            "" + (potPlayer.isCanSeeBroadcasts() ? "&a&l■ " : "&8&l■ ") + "&fEnabled",
                            "" + (!potPlayer.isCanSeeBroadcasts() ? "&a&l■ " : "&8&l■ ") + "&fDisabled"
                    )
                    .create()
            );
        }
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
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            switch (event.getRawSlot()) {
                case 2:
                    potPlayer.setCanSeeGlobalChat(!potPlayer.isCanSeeGlobalChat());
                    this.update();
                    break;
                case 3:
                    potPlayer.setCanSeeTips(!potPlayer.isCanSeeTips());
                    this.update();
                    break;
                case 4:
                    potPlayer.setCanReceiveDms(!potPlayer.isCanReceiveDms());
                    this.update();
                    break;
                case 5:
                    potPlayer.setCanReceiveDmsSounds(!potPlayer.isCanReceiveDmsSounds());
                    this.update();
                    break;
                case 6:
                    if (potPlayer.getPlayer().hasPermission("scandium.staff")) {
                        potPlayer.setCanSeeStaffMessages(!potPlayer.isCanSeeStaffMessages());
                    } else {
                        potPlayer.setCanSeeBroadcasts(!potPlayer.isCanSeeBroadcasts());
                    }
                    this.update();
                    break;
            }
        }
    }
}
