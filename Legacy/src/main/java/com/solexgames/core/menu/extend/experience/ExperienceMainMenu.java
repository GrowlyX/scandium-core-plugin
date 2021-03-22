package com.solexgames.core.menu.extend.experience;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.menu.extend.experience.buy.PrefixPurchaseMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.ExperienceUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class ExperienceMainMenu extends AbstractInventoryMenu {

    final private Player player;

    public ExperienceMainMenu(Player player) {
        super("Experience", 9);
        this.player = player;
        this.update();
    }

    @Override
    public void update() {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);

        this.inventory.setItem(4, new ItemBuilder(XMaterial.BLAZE_POWDER.parseMaterial())
                .setDisplayName("&6&lExperience")
                .addLore(
                        ChatColor.GRAY + "Welcome to the experience",
                        ChatColor.GRAY + "menu! With experience, you",
                        ChatColor.GRAY + "can purchase prefixes,",
                        ChatColor.GRAY + "temporary ranks, and more!",
                        "",
                        ChatColor.GRAY + "You currently have:",
                        ChatColor.YELLOW + String.valueOf(potPlayer.getExperience()) + " Experience",
                        "",
                        ChatColor.GREEN + "Click to open the shop menu!"
                )
                .create());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();

        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            if (event.getRawSlot() == 4) {
                if (CorePlugin.getInstance().getServerManager().getNetwork().equals(ServerType.POTCLUBVIP) || CorePlugin.getInstance().getServerManager().getNetwork().equals(ServerType.MINEARCADE)) {
                    new ExperienceShopMainMenu().open(this.player);
                }
            }
        }
    }
}
