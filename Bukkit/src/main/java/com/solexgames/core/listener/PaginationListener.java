package com.solexgames.core.listener;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class PaginationListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onButtonPress(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu != null && !event.getView().getTopInventory().getTitle().contains("Editing Loadout")) {
            if (event.getSlot() != event.getRawSlot()) {
                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                }

                return;
            }

            if (openMenu.getButtons().containsKey(event.getSlot())) {
                final Button button = openMenu.getButtons().get(event.getSlot());
                final boolean cancel = button.shouldCancel(player, event.getClick());

                if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);

                    if (event.getCurrentItem() != null) {
                        player.getInventory().addItem(event.getCurrentItem());
                    }
                } else {
                    event.setCancelled(cancel);
                }

                button.clicked(player, event.getClick());
                button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());

                if (Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                    final Menu newMenu = Menu.currentlyOpenedMenus.get(player.getName());

                    if (newMenu == openMenu) {
                        boolean buttonUpdate = button.shouldUpdate(player, event.getClick());

                        if (buttonUpdate) {
                            openMenu.setClosedByMenu(true);
                            newMenu.openMenu(player);
                        }
                    }
                } else if (button.shouldUpdate(player, event.getClick())) {
                    openMenu.setClosedByMenu(true);
                    openMenu.openMenu(player);
                }

                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), player::updateInventory, 1L);
                }
            } else {
                if (event.getCurrentItem() != null) {
                    event.setCancelled(true);
                }

                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                }
            }
        } else if (openMenu != null && event.getView().getTopInventory().getTitle().contains("Editing Loadout")) {
            if (event.getClickedInventory() == null) {
                event.setCancelled(true);
                return;
            }

            if (event.getClickedInventory().equals(player.getInventory())) {
                event.setCancelled(true);
            }

            if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu != null) {
            openMenu.onClose(player);

            Menu.currentlyOpenedMenus.remove(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrag(InventoryDragEvent event) {
        final Menu openMenu = Menu.currentlyOpenedMenus.get(event.getWhoClicked().getName());
        final Player player = (Player) event.getWhoClicked();

        if (openMenu != null && openMenu.getTitle(player).contains("Editing Loadout")) {
            event.setCancelled(false);
        } else if (openMenu != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMoveItem(InventoryMoveItemEvent event) {
        final Menu openMenu = Menu.currentlyOpenedMenus.get(event.getInitiator().getViewers().get(0).getName());
        final Player player = (Player) event.getInitiator().getViewers().get(0);

        if (openMenu != null && openMenu.getTitle(player).contains("Editing Loadout")) {
            if (event.getDestination().equals(player.getInventory()) || event.getDestination().equals(player.getOpenInventory().getTopInventory())) {
                event.setCancelled(true);
            }

            if (event.getSource().equals(player.getInventory()) || event.getSource().equals(player.getOpenInventory().getTopInventory())) {
                event.setCancelled(true);
            }

            if (event.getInitiator().equals(player.getInventory()) || event.getInitiator().equals(player.getOpenInventory().getTopInventory())) {
                event.setCancelled(true);
            }
        }
    }
}
