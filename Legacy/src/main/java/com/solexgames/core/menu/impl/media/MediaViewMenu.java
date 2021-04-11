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
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        this.inventory.setItem(1, new ItemBuilder(XMaterial.BLUE_DYE.parseMaterial())
                .setDurability(4)
                .setDisplayName("&9&lDiscord")
                .addLore(
                        ChatColor.GRAY + potPlayer.getMedia().getDiscord(),
                        "",
                        "&e[Click to view in chat]"
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(XMaterial.RED_DYE.parseMaterial())
                .setDurability(1)
                .setDisplayName("&c&lYouTube")
                .addLore(
                        ChatColor.GRAY + potPlayer.getMedia().getYoutubeLink(),
                        "",
                        "&e[Click to view in chat]"
                )
                .create()
        );
        this.inventory.setItem(5, new ItemBuilder(XMaterial.LIGHT_BLUE_DYE.parseMaterial())
                .setDurability(12)
                .setDisplayName("&b&lTwitter")
                .addLore(
                        ChatColor.GRAY + potPlayer.getMedia().getTwitter(),
                        "",
                        "&e[Click to view in chat]"
                )
                .create()
        );
        this.inventory.setItem(7, new ItemBuilder(XMaterial.ORANGE_DYE.parseMaterial())
                .setDurability(14)
                .setDisplayName("&6&lInstagram")
                .addLore(
                        ChatColor.GRAY + potPlayer.getMedia().getInstagram(),
                        "",
                        "&e[Click to view in chat]"
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
        }
    }
}
