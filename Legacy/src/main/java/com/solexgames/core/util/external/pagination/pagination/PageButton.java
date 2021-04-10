package com.solexgames.core.util.external.pagination.pagination;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PageButton extends Button {

    private final int mod;
    private final PaginatedMenu menu;

    @Override
    public ItemStack getButtonItem(Player player) {
        if (!this.hasNext(player)) {
            return new ItemBuilder(Material.AIR).create();
        }

        return new ItemBuilder(this.mod > 0 ? XMaterial.GLISTERING_MELON_SLICE.parseMaterial() : XMaterial.MELON_SLICE.parseMaterial())
                .setDisplayName(this.mod > 0 ? ChatColor.GREEN + "Next page" : ChatColor.RED + "Previous page" + ChatColor.GRAY + " (" + Color.MAIN_COLOR + (this.mod > 0 ? menu.getPage() + mod : menu.getPage() - mod) + ChatColor.GRAY + "/" + Color.MAIN_COLOR + menu.getPages(player) + ChatColor.GRAY + ")")
                .addLore(
                        "&7Right Click to view all pages!"
                ).create();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
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
