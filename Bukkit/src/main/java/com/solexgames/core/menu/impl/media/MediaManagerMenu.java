package com.solexgames.core.menu.impl.media;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class MediaManagerMenu extends AbstractInventoryMenu {

    private Player player;

    public MediaManagerMenu(Player player) {
        super("Social Media", 9);
        this.player = player;
        this.update();
    }

    public void update() {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);

        this.inventory.setItem(0, new ItemBuilder(XMaterial.BLUE_DYE.parseMaterial())
                .setDurability(4)
                .setDisplayName("&9&lDiscord")
                .addLore(
                        ChatColor.GRAY + potPlayer.getMedia().getDiscord()
                )
                .create()
        );
        this.inventory.setItem(1, new ItemBuilder(XMaterial.RED_DYE.parseMaterial())
                .setDurability(1)
                .setDisplayName("&c&lYouTube")
                .addLore(
                        ChatColor.GRAY + potPlayer.getMedia().getYoutubeLink()
                )
                .create()
        );
        this.inventory.setItem(2, new ItemBuilder(XMaterial.LIGHT_BLUE_DYE.parseMaterial())
                .setDurability(12)
                .setDisplayName("&b&lTwitter")
                .addLore(
                        ChatColor.GRAY + potPlayer.getMedia().getTwitter()
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(XMaterial.ORANGE_DYE.parseMaterial())
                .setDurability(14)
                .setDisplayName("&6&lInstagram")
                .addLore(
                        ChatColor.GRAY + potPlayer.getMedia().getInstagram()
                )
                .create()
        );
        this.inventory.setItem(8, new ItemBuilder(XMaterial.RED_BED.parseMaterial())
                .setDisplayName("&a&lModify Values")
                .addLore(
                        "&7Modify your social media",
                        "&7values for your profile!",
                        "",
                        "&e[Click to open menu]"
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
            Player player = (Player) event.getWhoClicked();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            if (event.getRawSlot() == 8) {
                new MediaSetMenu(player).open(player);
            }
        }
    }
}
