package com.solexgames.core.menu.impl.lang;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.LanguageType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class LanguageMenu extends AbstractInventoryMenu {

    private Player player;

    public LanguageMenu(Player player) {
        super("Language", 9);
        this.player = player;

    }

    public void update() {
        this.inventory.setItem(2, new ItemBuilder(XMaterial.YELLOW_DYE.parseMaterial(), 11)
                .setDisplayName("&eEnglish")
                .addLore(
                        "&7Would you like to see",
                        "&7messages in English?"
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(XMaterial.ORANGE_DYE.parseMaterial(), 14)
                .setDisplayName("&6Español")
                .addLore(
                        "&7¿Le gustaría ver mensajes",
                        "&7en español?"
                )
                .create()
        );
        this.inventory.setItem(4, new ItemBuilder(XMaterial.LIGHT_BLUE_DYE.parseMaterial(), 12)
                .setDisplayName("&bFrançais")
                .addLore(
                        "&7Souhaitez-vous voir",
                        "&7les messages en français?"
                )
                .create()
        );
        this.inventory.setItem(5, new ItemBuilder(XMaterial.LIME_DYE.parseMaterial(), 10)
                .setDisplayName("&aItaliano")
                .addLore(
                        "&7Vuoi vedere i messaggi",
                        "&7in italiano?"
                )
                .create()
        );
        this.inventory.setItem(6, new ItemBuilder(XMaterial.RED_DYE.parseMaterial(), 1)
                .setDisplayName("&cDeutsch")
                .addLore(
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
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            switch (event.getRawSlot()) {
                case 2:
                    potPlayer.setLanguage(LanguageType.ENGLISH);
                    this.player.sendMessage(ChatColor.GREEN + Color.translate("Your language has been set to &eEnglish&a."));
                    break;
                case 3:
                    potPlayer.setLanguage(LanguageType.SPANISH);
                    this.player.sendMessage(ChatColor.GREEN + Color.translate("Your language has been set to &6Spanish&a."));
                    break;
                case 4:
                    potPlayer.setLanguage(LanguageType.FRENCH);
                    this.player.sendMessage(ChatColor.GREEN + Color.translate("Your language has been set to &bFrench&a."));
                    break;
                case 5:
                    potPlayer.setLanguage(LanguageType.ITALIAN);
                    this.player.sendMessage(ChatColor.GREEN + Color.translate("Your language has been set to &aItalian&a."));
                    break;
                case 6:
                    potPlayer.setLanguage(LanguageType.GERMAN);
                    this.player.sendMessage(ChatColor.GREEN + Color.translate("Your language has been set to &cGerman&a."));
                    break;
            }
            player.closeInventory();
        }
    }
}
