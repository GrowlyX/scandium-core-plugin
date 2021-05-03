package com.solexgames.core.util.external.button;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class JumpToPageButton extends Button {

    private final int page;
    private final PaginatedMenu menu;
    private final boolean current;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> stringList = new ArrayList<>();

        stringList.add(ChatColor.GRAY + "Click to switch to this menu!");

        if (this.current) {
            stringList.add("  ");
            stringList.add(ChatColor.GREEN + "This is the current page.");
        }

        return new ItemBuilder(this.current ? XMaterial.ENCHANTED_BOOK.parseMaterial() : XMaterial.BOOK.parseMaterial())
                .addLore(stringList)
                .setDisplayName(ChatColor.YELLOW + "Page " + this.page)
                .create();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        this.menu.modPage(player, this.page - this.menu.getPage());
    }
}
