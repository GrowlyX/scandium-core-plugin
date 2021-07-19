package com.solexgames.core.menu.impl.media;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@Getter
public class MediaViewMenu extends AbstractInventoryMenu {

    private final Player player;

    public MediaViewMenu(Player player) {
        super("Social Media for: " + player.getDisplayName(), 9);
        this.player = player;
        this.update();
    }

    public void update() {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);
        final boolean disguised = potPlayer.isDisguised();

        this.inventory.setItem(1, new ItemBuilder(XMaterial.BLUE_DYE.parseMaterial())
                .setDurability(4)
                .setDisplayName("&9&lDiscord")
                .addLore(
                        ChatColor.GRAY + (disguised ? "N/A" : potPlayer.getMedia().getDiscord())
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(XMaterial.RED_DYE.parseMaterial())
                .setDurability(1)
                .setDisplayName("&c&lYouTube")
                .addLore(
                        ChatColor.GRAY + (disguised ? "N/A" : potPlayer.getMedia().getYoutubeLink())
                )
                .create()
        );
        this.inventory.setItem(5, new ItemBuilder(XMaterial.LIGHT_BLUE_DYE.parseMaterial())
                .setDurability(12)
                .setDisplayName("&b&lTwitter")
                .addLore(
                        ChatColor.GRAY + (disguised ? "N/A" : potPlayer.getMedia().getTwitter())
                )
                .create()
        );
        this.inventory.setItem(7, new ItemBuilder(XMaterial.ORANGE_DYE.parseMaterial())
                .setDurability(14)
                .setDisplayName("&6&lInstagram")
                .addLore(
                        ChatColor.GRAY + (disguised ? "N/A" : potPlayer.getMedia().getInstagram())
                )
                .create()
        );
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        final Inventory topInventory = event.getView().getTopInventory();

        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);
        }
    }
}
