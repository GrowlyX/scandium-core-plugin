package vip.potclub.core.menu.extend.media;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;

@Getter
public class ExternalMediaMenu extends AbstractInventoryMenu<CorePlugin> {

    private final Player player;

    public ExternalMediaMenu(Player player) {
        super("Media - " + player.getName(), 9);
        this.player = player;
        this.update();
    }

    private void update() {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        this.inventory.setItem(1, new InventoryMenuItem(Material.INK_SACK, 4)
                .setDisplayName("&9Discord")
                .addLore(
                        "",
                        "&7Their discord:",
                        "&f" + potPlayer.getMedia().getDiscord(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(3, new InventoryMenuItem(Material.INK_SACK, 1)
                .setDisplayName("&cYouTube")
                .addLore(
                        "",
                        "&7Their youtube:",
                        "&f" + potPlayer.getMedia().getYoutubeLink(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(5, new InventoryMenuItem(Material.INK_SACK, 12)
                .setDisplayName("&bTwitter")
                .addLore(
                        "",
                        "&7Their twitter:",
                        "&f" + potPlayer.getMedia().getTwitter(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(7, new InventoryMenuItem(Material.INK_SACK, 14)
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
