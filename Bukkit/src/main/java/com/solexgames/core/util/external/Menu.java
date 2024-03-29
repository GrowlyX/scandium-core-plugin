package com.solexgames.core.util.external;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class Menu {

    @Getter
    public static Map<String, Menu> currentlyOpenedMenus = new HashMap<>();

    private Map<Integer, Button> buttons = new HashMap<>();

    private boolean autoUpdate = false;
    private boolean updateAfterClick = true;
    private boolean closedByMenu = false;
    private boolean placeholder = false;
    private boolean fillBorders = false;

    public void openMenu(Player player) {
        this.buttons = this.getButtons(player);

        final Menu previousMenu = Menu.currentlyOpenedMenus.get(player.getName());

        Inventory inventory = null;

        final int size = this.getSize() == -1 ? this.size(this.buttons) : this.getSize();
        boolean update = false;

        String title = Color.translate(this.getTitle(player));

        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        if (player.getOpenInventory() != null) {
            if (previousMenu == null) {
                player.closeInventory();
            } else {
                int previousSize = player.getOpenInventory().getTopInventory().getSize();

                // int previousSize = player.getOpenInventory().getTopInventory().getSize();
                //                String previousTitle = player.getOpenInventory().getTopInventory().getTitle();
                //
                //                System.out.println("previousTitle= " + previousTitle);
                //                System.out.println("newTitle= " + this.getRawTitle(player));
                ////                System.out.println("newTitleSubstring= " + (this.getRawTitle(player).substring(0, previousTitle.length())));
                //
                //                if ((previousTitle.substring(0, this.getRawTitle(player).length()).equalsIgnoreCase(this.getRawTitle(player))) || previousSize == size && player.getOpenInventory().getTopInventory().getTitle().equals(title)) {
                //                    inventory = player.getOpenInventory().getTopInventory();
                //                    update = true;
                //                } else {
                //                    previousMenu.setClosedByMenu(true);
                //                    player.closeInventory();
                //                }

                if (previousSize == size && player.getOpenInventory().getTopInventory().getTitle().equals(title)) {
                    inventory = player.getOpenInventory().getTopInventory();
                    update = true;
                } else {
                    previousMenu.setClosedByMenu(true);
                    player.closeInventory();
                }
            }
        }

        if (inventory == null) {
            inventory = Bukkit.createInventory(player, size, title);
        }

        inventory.setContents(new ItemStack[inventory.getSize()]);

        Menu.currentlyOpenedMenus.put(player.getName(), this);

        final Button placeholderButton = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial())
                .setDurability(15)
                .setDisplayName("  ")
                .toButton();

        if (this.isFillBorders()) {
            for (int i = 0; i < this.getSize(); i++) {
                if (i < 9 || i >= this.getSize() - 9 || i % 9 == 0 || i % 9 == 8) {
                    inventory.setItem(i, placeholderButton.getButtonItem(player));
                }
            }
        }

        for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet()) {
            inventory.setItem(buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(player));
        }

        if (this.isPlaceholder()) {
            for (int index = 0; index < size; index++) {
                if (this.buttons.get(index) == null) {
                    this.buttons.put(index, placeholderButton);

                    inventory.setItem(index, placeholderButton.getButtonItem(player));
                }
            }
        }

        if (update) {
            player.updateInventory();
        } else {
            this.onOpen(player);
            player.openInventory(inventory);
        }

        this.setClosedByMenu(false);
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;

        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    public void onClose(Player player) {

    }

    public void onOpen(Player player) {

    }

    public int getSize() {
        return -1;
    }

    public abstract String getTitle(Player player);

    public String getRawTitle(Player player) {
        return this.getTitle(player);
    }

    public abstract Map<Integer, Button> getButtons(Player player);

}
