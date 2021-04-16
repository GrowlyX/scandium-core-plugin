package com.solexgames.core.util.external.pagination.button;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.Menu;
import com.solexgames.core.util.callback.AsyncCallback;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class ConfirmationButton extends Button {

    private final boolean confirm;
    private final AsyncCallback callback;
    private final boolean closeAfterResponse;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(XMaterial.WHITE_WOOL.parseMaterial())
                .setDurability(this.confirm ? 5 : 14)
                .setDisplayName(this.confirm ? ChatColor.GREEN + "Confirm" : ChatColor.RED + "Cancel")
                .create();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (this.confirm) {
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 20f, 0.1f);
        } else {
            player.playSound(player.getLocation(), XSound.BLOCK_GRAVEL_BREAK.parseSound(), 20f, 0.1F);
        }

        if (this.closeAfterResponse) {
            Menu menu = Menu.currentlyOpenedMenus.get(player.getName());

            if (menu != null) {
                menu.setClosedByMenu(true);
            }

            player.closeInventory();
        }
    }
}
