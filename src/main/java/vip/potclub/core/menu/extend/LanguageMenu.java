package vip.potclub.core.menu.extend;

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
public class LanguageMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;

    public LanguageMenu(Player player) {
        super("Settings", 9);
        this.player = player;
        this.update();
    }

    private void update() {
        this.inventory.setItem(2, new InventoryMenuItem(Material.WOOL, 4)
                .setDisplayName("&eEnglish")
                .addLore(
                        "",
                        "&7Would you like to see",
                        "&7messages in English?"
                )
                .create()
        );
        this.inventory.setItem(3, new InventoryMenuItem(Material.WOOL, 1)
                .setDisplayName("&6Español")
                .addLore(
                        "",
                        "&7¿Le gustaría ver mensajes",
                        "&7en español?"
                )
                .create()
        );
        this.inventory.setItem(4, new InventoryMenuItem(Material.WOOL, 3)
                .setDisplayName("&bFrançais")
                .addLore(
                        "",
                        "&7Souhaitez-vous voir",
                        "&7les messages en français?"
                )
                .create()
        );
        this.inventory.setItem(5, new InventoryMenuItem(Material.WOOL, 5)
                .setDisplayName("&aItaliano")
                .addLore(
                        "",
                        "&7Vuoi vedere i messaggi",
                        "&7in italiano?"
                )
                .create()
        );
        this.inventory.setItem(6, new InventoryMenuItem(Material.WOOL, 14)
                .setDisplayName("&cDeutsch")
                .addLore(
                        "",
                        "&7Möchten Sie Nachrichten",
                        "&7auf Deutsch sehen?"
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
                    potPlayer.setLanguage(LanguageType.ENGLISH);
                    player.sendMessage(Color.translate("&aYour language has been set to &eEnglish&a."));
                    break;
                case 3:
                    potPlayer.setLanguage(LanguageType.SPANISH);
                    player.sendMessage(Color.translate("&aYour language has been set to &6Spanish&a."));
                    break;
                case 4:
                    potPlayer.setLanguage(LanguageType.FRENCH);
                    player.sendMessage(Color.translate("&aYour language has been set to &bFrench&a."));
                    break;
                case 5:
                    potPlayer.setLanguage(LanguageType.ITALIAN);
                    player.sendMessage(Color.translate("&aYour language has been set to &aItalian&a."));
                    break;
                case 6:
                    potPlayer.setLanguage(LanguageType.GERMAN);
                    player.sendMessage(Color.translate("&aYour language has been set to &cGerman&a."));
                    break;
            }
        }
    }
}
