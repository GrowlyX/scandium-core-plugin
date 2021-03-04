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
public class JumpToPageButton extends Button {

    private final int page;
    private final PaginatedMenu menu;
    private final boolean current;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(this.current ? XMaterial.ENCHANTED_BOOK.parseMaterial() : XMaterial.BOOK.parseMaterial())
                .addLore(this.current ? "&7You are on page &b" + this.page + "&7!" : "&aClick to jump to this menu!")
                .setDisplayName(this.current ? ChatColor.GREEN + "Current Page" : ChatColor.YELLOW + "Page " + this.page)
                .create();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        this.menu.modPage(player, this.page - this.menu.getPage());
        Button.playNeutral(player);
    }

}
