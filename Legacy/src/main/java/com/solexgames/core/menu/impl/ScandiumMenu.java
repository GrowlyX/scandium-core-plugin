package com.solexgames.core.menu.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class ScandiumMenu extends AbstractInventoryMenu {

    private Player player;

    public ScandiumMenu(Player player) {
        super("Control Panel", 9);

        this.player = player;

        this.update();
    }

    public void update() {
        this.inventory.setItem(2, new ItemBuilder(XMaterial.YELLOW_DYE.parseMaterial(), 11)
                .setDisplayName("&6Reload Files")
                .addLore(
                        "&7Would you like to reload",
                        "&7configurations?",
                        "",
                        "&aClick to reload files."
                )
                .create()
        );

        this.inventory.setItem(4, new ItemBuilder(XMaterial.CYAN_DYE.parseMaterial(), 6)
                .setDisplayName("&bScandium Core")
                .addLore(
                        "&7You are currently running",
                        "&7Scandium core by GrowlyX!",
                        "  ",
                        "&7Support: &6GrowlyX#1337",
                        "&7Pricing: &b$50",
                        "",
                        "&aClick to contact GrowlyX."
                )
                .create()
        );

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        this.inventory.setItem(6, new ItemBuilder(XMaterial.ORANGE_DYE.parseMaterial(), 14)
                .setDisplayName("&b&lInformation &7(" + network.getServerName() + ")")
                .addLore(
                        "&7Server Name: &f" + network.getServerName(),
                        "&7Server ID: &f" + network.getServerId(),
                        "&7Primary Color: &f" + network.getMainColor() + "Color One",
                        "&7Secondary Color: &f" + network.getSecondaryColor() + "Color Two",
                        "&7Discord Link: &f" + network.getDiscordLink(),
                        "&7Store Link: &f" + network.getStoreLink(),
                        "&7Twitter Link: &f" + network.getTwitterLink(),
                        "&7Website Link: &f" + network.getWebsiteLink()
                )
                .create()
        );
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            if (event.getRawSlot() == 2) {
                CorePlugin.getInstance().reloadConfig();
                player.sendMessage(ChatColor.GREEN + Color.translate("Reloaded the main config!"));
            }
            if (event.getRawSlot() == 4) {
                player.sendMessage(ChatColor.GREEN + Color.translate("Contact: &6https://dsc.bio/GrowlyX/."));
            }
        }
    }
}
