package com.solexgames.core.menu.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.StringUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.external.impl.network.NetworkServerInfoMenu;
import com.solexgames.core.util.external.impl.network.NetworkServerMainMenu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

@Getter
@Setter
public class ScandiumMenu extends AbstractInventoryMenu {

    private Player player;

    public ScandiumMenu(Player player) {
        super("Control panel", 9);

        this.player = player;

        this.update();
    }

    public void update() {
        final ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(3, new ItemBuilder(XMaterial.PISTON.parseMaterial(), 6)
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + network.getServerName())
                .addLore(
                        "&7Thanks for using Scandium!",
                        "&7Below is some of the information",
                        "&7we store about your network.",
                        "",
                        "&7Primary: " + Color.SECONDARY_COLOR + ChatColor.BOLD.toString() + "■",
                        "&7Secondary: " + Color.MAIN_COLOR + ChatColor.BOLD.toString() + "■",
                        "",
                        "&7Domain: " + ChatColor.WHITE + network.getWebsiteLink(),
                        "&7Store: " + ChatColor.WHITE + network.getStoreLink(),
                        "&7Discord: " + ChatColor.WHITE + network.getDiscordLink(),
                        "",
                        "&7Version: " + Color.SECONDARY_COLOR + CorePlugin.getInstance().getDescription().getVersion(),
                        "",
                        "&e[Click to reload config files]"
                )
                .create()
        );

        this.inventory.setItem(5, new ItemBuilder(XMaterial.REDSTONE_LAMP.parseMaterial(), 11)
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Instances")
                .addLore(
                        "&7Would you like to view",
                        "&7all server instances ",
                        "&7currently running scandium?",
                        "",
                        "&f&oOnly applies to this",
                        "&f&ojedis instance!",
                        "",
                        "&e[Click to view instances]"
                )
                .create()
        );
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        final Inventory topInventory = event.getView().getTopInventory();

        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            final ItemStack item = event.getCurrentItem();
            final Player player = (Player) event.getWhoClicked();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            if (event.getRawSlot() == 3) {
                CorePlugin.getInstance().reloadConfig();
                player.sendMessage(ChatColor.GREEN + "The main config has been reloaded.");

                player.closeInventory();
            }
            if (event.getRawSlot() == 5) {
                new NetworkServerMainMenu().openMenu(player);
            }
        }
    }
}
