package com.solexgames.core.menu.extend.media;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@Getter
public class ExternalMediaMenu extends AbstractInventoryMenu {

    private final Player player;

    public ExternalMediaMenu(Player player) {
        super("Media - " + player.getName(), 9);
        this.player = player;
        this.update();
    }

    public void update() {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        this.inventory.setItem(1, new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
                .setDurability(4)
                .setDisplayName("&9Discord")
                .addLore(
                        "",
                        "&7Their discord:",
                        "&f" + potPlayer.getMedia().getDiscord(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
                .setDurability(1)
                .setDisplayName("&cYouTube")
                .addLore(
                        "",
                        "&7Their youtube:",
                        "&f" + potPlayer.getMedia().getYoutubeLink(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(5, new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
                .setDurability(12)
                .setDisplayName("&bTwitter")
                .addLore(
                        "",
                        "&7Their twitter:",
                        "&f" + potPlayer.getMedia().getTwitter(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(7, new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
                .setDurability(14)
                .setDisplayName("&6Instagram")
                .addLore(
                        "",
                        "&7Their instagram:",
                        "&f" + potPlayer.getMedia().getInstagram(),
                        ""
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
