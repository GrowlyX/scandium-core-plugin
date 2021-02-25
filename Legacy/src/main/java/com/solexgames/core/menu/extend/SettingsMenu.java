package com.solexgames.core.menu.extend;

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
        this.inventory.setItem(2, new ItemBuilder(Material.PAPER)
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
        this.inventory.setItem(3, new ItemBuilder(Material.EXP_BOTTLE)
                .setDisplayName("&3Server Tips")
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
        this.inventory.setItem(4, new ItemBuilder(Material.EMERALD)
                .setDisplayName("&3Receive DMs")
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
        this.inventory.setItem(5, new ItemBuilder(Material.JUKEBOX)
                .setDisplayName("&3DMs Sounds")
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
            this.inventory.setItem(6, new ItemBuilder(Material.BLAZE_POWDER)
                    .setDisplayName("&3Staff Messages")
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
            this.inventory.setItem(6, new ItemBuilder(Material.BLAZE_POWDER)
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

            if (item == null || item.getType() == Material.AIR) return;
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