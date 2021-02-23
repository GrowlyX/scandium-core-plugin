package com.solexgames.core.menu.extend.modsuite;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class StaffMenu extends AbstractInventoryMenu {

    public Player player;

    public StaffMenu(Player player) {
        super("Online Staff", 9*3);

        this.player = player;

        this.update();
    }

    @Override
    public void update() {
        AtomicInteger integer = new AtomicInteger();
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        Bukkit.getOnlinePlayers().stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(Objects::nonNull)
                .filter(potPlayer -> potPlayer.getPlayer().hasPermission("scandium.staff"))
                .forEach(potPlayer -> this.inventory.setItem(integer.getAndIncrement(), new ItemBuilder(Material.SKULL_ITEM)
                        .setOwner(potPlayer.getName())
                        .setDisplayName(Color.translate(potPlayer.getColorByRankColor() + potPlayer.getPlayer().getName()))
                        .addLore(
                                network.getMainColor() + "&m------------------------",
                                network.getSecondaryColor() + "Mod-Mode: " + (potPlayer.isStaffMode() ? "&aEnabled" : "&cDisabled"),
                                network.getSecondaryColor() + "Vanish: " + (potPlayer.isVanished() ? "&aEnabled" : "&cDisabled"),
                                network.getSecondaryColor() + "Discord: " + (potPlayer.getSyncDiscord() != null ? network.getMainColor() + potPlayer.getSyncDiscord() : "&cNot Synced"),
                                "",
                                "&aClick to teleport to " + potPlayer.getColorByRankColor() + potPlayer.getPlayer().getName() + "&a!",
                                network.getMainColor() + "&m------------------------"
                        )
                        .create()
                ));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName() != null) {
                    String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                    if (Bukkit.getPlayer(display) != null) {
                        Player clickedUser = Bukkit.getPlayer(display);
                        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(clickedUser);

                        this.player.teleport(clickedUser.getLocation());
                        this.player.sendMessage(Color.translate("&aYou have been teleported to " + potPlayer.getColorByRankColor() + potPlayer.getPlayer().getName() + "&a!"));
                    }
                }
            }
        }
    }
}
