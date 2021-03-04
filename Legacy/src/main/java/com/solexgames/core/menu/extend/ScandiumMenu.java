package com.solexgames.core.menu.extend;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.UUIDUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
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
        this.inventory.setItem(2, new ItemBuilder(XMaterial.INK_SAC.parseMaterial(), 11)
                .setDisplayName("&eReload Files")
                .addLore(
                        "",
                        "&7Would you like to reload",
                        "&7configurations?",
                        "",
                        "&eClick to reload files."
                )
                .create()
        );

        this.inventory.setItem(4, new ItemBuilder(XMaterial.INK_SAC.parseMaterial(), 6)
                .setDisplayName("&bScandium Core")
                .addLore(
                        "",
                        "&7Thanks for purchasing",
                        "&7Scandium core!",
                        "  ",
                        "&7Support: &bGrowlyX#1337",
                        "&7Pricing: &b$45",
                        "",
                        "&eClick to contact GrowlyX."
                )
                .create()
        );

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        this.inventory.setItem(6, new ItemBuilder(XMaterial.INK_SAC.parseMaterial(), 14)
                .setDisplayName("&bNetwork Info &7(" + network.getServerName() + ")")
                .addLore(
                        "",
                        "&7Server Name: &f" + network.getServerName(),
                        "&7Server ID: &f" + network.getServerId(),
                        "&7Primary Color: &f" + network.getMainColor() + "Color One",
                        "&7Secondary Color: &f" + network.getSecondaryColor() + "Color Two",
                        "&7Discord Link: &f" + network.getDiscordLink(),
                        "&7Store Link: &f" + network.getStoreLink(),
                        "&7Twitter Link: &f" + network.getTwitterLink(),
                        "&7Website Link: &f" + network.getWebsiteLink(),
                        ""
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
                player.sendMessage(Color.translate("&aReloaded the main config!"));
            }
            if (event.getRawSlot() == 4) {
                player.sendMessage(Color.translate("&aContact: &6https://dsc.bio/GrowlyX/."));
            }
        }
    }
}
