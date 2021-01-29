package vip.potclub.core.menu.extend.media;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.LanguageType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

@Getter
@Setter
public class SetMediaMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;

    public SetMediaMenu(Player player) {
        super("Media", 9);
        this.player = player;
        this.update();
    }

    private void update() {
        this.inventory.setItem(2, new InventoryMenuItem(Material.INK_SACK, 4)
                .setDisplayName("&9Discord")
                .addLore(
                        "",
                        "&7Click here to setup",
                        "&7your discord account!",
                        ""
                )
                .create()
        );
        this.inventory.setItem(3, new InventoryMenuItem(Material.INK_SACK, 1)
                .setDisplayName("&cYouTube")
                .addLore(
                        "",
                        "&7Click here to setup",
                        "&7your youtube account!",
                        ""
                )
                .create()
        );
        this.inventory.setItem(4, new InventoryMenuItem(Material.INK_SACK, 12)
                .setDisplayName("&bTwitter")
                .addLore(
                        "",
                        "&7Click here to setup",
                        "&7your twitter account!",
                        ""
                )
                .create()
        );
        this.inventory.setItem(5, new InventoryMenuItem(Material.INK_SACK, 14)
                .setDisplayName("&6Instagram")
                .addLore(
                        "",
                        "&7Click here to setup",
                        "&7your insta account!",
                        ""
                )
                .create()
        );
        this.inventory.setItem(6, new InventoryMenuItem(Material.INK_SACK, 11)
                .setDisplayName("&e???")
                .addLore(
                        "",
                        "&7Click here to setup",
                        "&7your ??? account!",
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
            PotPlayer potPlayer = PotPlayer.getPlayer(player);

            if (item == null || item.getType() == Material.AIR) return;
            switch (event.getRawSlot()) {
                case 2:
                    potPlayer.getMedia().getMediaData().setModifyingDiscordData(true);
                    player.sendMessage(Color.translate("&aType your &9Discord &ausername in chat!"));
                    player.closeInventory();
                    break;
                case 3:
                    potPlayer.getMedia().getMediaData().setModifyingYoutubeData(true);
                    player.sendMessage(Color.translate("&aType your &cYouTube &achannel link in chat!"));
                    player.closeInventory();
                    break;
                case 4:
                    potPlayer.getMedia().getMediaData().setModifyingTwitterData(true);
                    player.sendMessage(Color.translate("&aType your &bTwitter &ausername in chat!"));
                    player.closeInventory();
                    break;
                case 5:
                    potPlayer.getMedia().getMediaData().setModifyingInstaData(true);
                    player.sendMessage(Color.translate("&aType your &6Instagram &ausername in chat!"));
                    player.closeInventory();
                    break;
                case 6:
                    player.sendMessage(Color.translate("&cThis button is currently disabled."));
                    player.closeInventory();
                    break;
            }
        }
    }
}
