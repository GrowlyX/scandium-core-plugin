package com.solexgames.core.util.external.pagination.pagination;

import com.cryptomorin.xseries.XMaterial;
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
            return new ItemBuilder(XMaterial.RED_DYE.parseMaterial())
                    .setDurability(1)
                    .setDisplayName(ChatColor.RED + "Last page")
                    .addLore(
                            "&7You're at the last page!",
                            "",
                            "&7Click this button to",
                            "&7return to the first page."
                    ).create();
        }

        return new ItemBuilder(this.mod > 0 ? XMaterial.GLISTERING_MELON_SLICE.parseMaterial() : XMaterial.MELON_SLICE.parseMaterial())
                .setDisplayName(this.mod > 0 ? ChatColor.GREEN + "Next page" : ChatColor.RED + "Return")
                .addLore(
                        "&7Click to go to the " + (this.mod > 0 ? "next" : "previous"),
                        "&7page."
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
