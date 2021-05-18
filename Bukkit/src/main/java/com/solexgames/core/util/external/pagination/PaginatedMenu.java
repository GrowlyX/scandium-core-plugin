package com.solexgames.core.util.external.pagination;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.button.ViewAllPagesMenu;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public abstract class PaginatedMenu extends Menu {

    private final int maxPerPage;

    @Getter
    private int page = 1; {
        this.setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return getPrePaginatedTitle(player) + ChatColor.GRAY + " (" + Color.MAIN_COLOR + page + ChatColor.GRAY + "/" + Color.MAIN_COLOR + this.getPages(player) + ChatColor.GRAY + ")";
    }

    /**
     * Changes the page number
     *
     * @param player player viewing the inventory
     * @param mod    delta to modify the page number by
     */
    public final void modPage(Player player, int mod) {
        page += mod;

        this.getButtons().clear();
        this.openMenu(player);
    }

    /**
     * Changes the page number to 1
     */
    public final void reset(Player player) {
        page = 1;

        this.getButtons().clear();
        this.openMenu(player);
    }


    /**
     * @param player player viewing the inventory
     */
    public final int getPages(Player player) {
        int buttonAmount = getAllPagesButtons(player).size();

        if (buttonAmount == 0) {
            return 1;
        }

        return (int) Math.ceil(buttonAmount / (double) maxPerPage);
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        final int minIndex = (int) ((double) (this.page - 1) * this.maxPerPage);
        final int maxIndex = (int) ((double) (this.page) * this.maxPerPage);

        final HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 1; i <= 7; i++) {
            buttons.put(i, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial())
                    .setDurability(7)
                    .setDisplayName(" ")
                    .toButton());
        }

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));

        for (final Map.Entry<Integer, Button> entry : this.getAllPagesButtons(player).entrySet()) {
            int ind = entry.getKey();

            if (ind >= minIndex && ind < maxIndex) {
                ind -= (int) ((double) (this.maxPerPage) * (this.page - 1)) - 9;
                buttons.put(ind, entry.getValue());
            }
        }

        final Map<Integer, Button> global = this.getGlobalButtons(player);

        if (global != null) {
            for (final Map.Entry<Integer, Button> gent : global.entrySet()) {
                buttons.put(gent.getKey(), gent.getValue());
            }
        }

        return buttons;
    }

    /**
     * @param player player viewing the inventory
     * @return a Map of button that returns items which will be present on all pages
     */
    public abstract Map<Integer, Button> getGlobalButtons(Player player);

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    public abstract String getPrePaginatedTitle(Player player);

    /**
     * @param player player viewing the inventory
     * @return a map of button that will be paginated and spread across pages
     */
    public abstract Map<Integer, Button> getAllPagesButtons(Player player);

    @AllArgsConstructor
    public static class PageButton extends Button {

        private final int mod;
        private final PaginatedMenu menu;

        @Override
        public ItemStack getButtonItem(Player player) {
            if (!this.hasNext(player)) {
                return new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial())
                        .setDurability(7)
                        .setDisplayName(" ")
                        .create();
            }

            return new ItemBuilder(this.mod > 0 ? XMaterial.GLISTERING_MELON_SLICE.parseMaterial() : XMaterial.MELON_SLICE.parseMaterial())
                    .setDisplayName((this.mod > 0 ? ChatColor.GREEN + "Next page" : ChatColor.RED + "Previous page") + ChatColor.GRAY + " (" + Color.MAIN_COLOR + (menu.getPage() + mod) + ChatColor.GRAY + "/" + Color.MAIN_COLOR + menu.getPages(player) + ChatColor.GRAY + ")")
                    .addLore(
                            "&7Right Click to view all pages!"
                    ).create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.equals(ClickType.RIGHT) && this.hasNext(player)) {
                new ViewAllPagesMenu(this.menu).openMenu(player);
            } else {
                if (this.hasNext(player)) {
                    this.menu.modPage(player, this.mod);
                } else {
                    this.menu.reset(player);
                }
            }
        }

        private boolean hasNext(Player player) {
            int pg = this.menu.getPage() + this.mod;
            return pg > 0 && this.menu.getPages(player) >= pg;
        }
    }
}
