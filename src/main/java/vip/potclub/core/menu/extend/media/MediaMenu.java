package vip.potclub.core.menu.extend.media;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;

@Getter
@Setter
public class MediaMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;

    public MediaMenu(Player player) {
        super("Media", 9);
        this.player = player;
        this.update();
    }

    private void update() {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        this.inventory.setItem(0, new InventoryMenuItem(Material.INK_SACK, 4)
                .setDisplayName("&9Discord")
                .addLore(
                        "",
                        "&7Your discord:",
                        "&f" + potPlayer.getMedia().getDiscord(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(1, new InventoryMenuItem(Material.INK_SACK, 1)
                .setDisplayName("&cYouTube")
                .addLore(
                        "",
                        "&7Your youtube:",
                        "&f" + potPlayer.getMedia().getYoutubeLink(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(2, new InventoryMenuItem(Material.INK_SACK, 12)
                .setDisplayName("&bTwitter")
                .addLore(
                        "",
                        "&7Your twitter:",
                        "&f" + potPlayer.getMedia().getTwitter(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(3, new InventoryMenuItem(Material.INK_SACK, 14)
                .setDisplayName("&6Instagram")
                .addLore(
                        "",
                        "&7Your instagram:",
                        "&f" + potPlayer.getMedia().getInstagram(),
                        ""
                )
                .create()
        );
        this.inventory.setItem(8, new InventoryMenuItem(Material.NETHER_STAR)
                .setDisplayName("&eModify Values")
                .addLore(
                        "",
                        "&7Click to modify your",
                        "&7social media!",
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

            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();

            if (item == null || item.getType() == Material.AIR) return;
            if (event.getRawSlot() == 8) {
                new SetMediaMenu(player).open(player);
            }
        }
    }
}
