package com.solexgames.core.util.external.pagination.button;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
@Setter
public class DisplayButton extends Button {

    private ItemStack itemStack;
    private boolean cancel;

    @Override
    public ItemStack getButtonItem(Player player) {
        if (this.itemStack == null) {
            return new ItemBuilder(XMaterial.AIR.parseMaterial()).create();
        } else {
            return this.itemStack;
        }
    }

    @Override
    public boolean shouldCancel(Player player, ClickType clickType) {
        return this.cancel;
    }
}
