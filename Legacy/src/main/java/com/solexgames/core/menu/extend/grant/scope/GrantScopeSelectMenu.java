package com.solexgames.core.menu.extend.grant.scope;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.menu.extend.grant.GrantMainMenu;
import com.solexgames.core.menu.extend.grant.GrantSelectDurationMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GrantScopeSelectMenu extends AbstractInventoryMenu {

    private final Player player;
    private final Document document;
    private final Rank rank;

    public GrantScopeSelectMenu(Player player, Document document, Rank rank) {
        super("Select grant scope for: " + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name")), 9*5);

        this.rank = rank;
        this.player = player;
        this.document = document;

        this.update();
    }

    @Override
    public void update() {
        int[] stained = new int[] { 0,1,2,6,7,8 };

        for (int i : stained) {
            this.inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").create());
        }

        this.inventory.setItem(3, new ItemBuilder(Material.STAINED_CLAY, 13).setDisplayName("&aGlobal Scope").addLore(Arrays.asList("", "&7Click to select the global", "&7scope for this grant.")).create());
        this.inventory.setItem(4, new ItemBuilder(Material.STAINED_CLAY, 9).setDisplayName("&bScope Selection").addLore(Arrays.asList("", "&7Use the buttons below to", "&7select a server for this", "&7grant to be applied on!")).create());
        this.inventory.setItem(5, new ItemBuilder(Material.STAINED_CLAY, 14).setDisplayName("&cReturn to Main").addLore(Arrays.asList("", "&7Click to cancel this grant", "&7and return to the main menu.")).create());

        AtomicInteger i = new AtomicInteger(19);

        CorePlugin.getInstance().getServerManager().getNetworkServers().stream()
                .filter(Objects::nonNull)
                .forEach(server -> {
                    if (i.get() <= 34) {
                        this.inventory.setItem(i.get(), new ItemBuilder(Material.PAPER)
                                .setDisplayName(ChatColor.GREEN + server.getServerName())
                                .addLore(
                                        "",
                                        "&7Click to select this server",
                                        "&7for the grant scope."
                                )
                                .create()
                        );

                        if ((i.get() == 25)) {
                            i.getAndIncrement();
                            i.getAndIncrement();
                            i.getAndIncrement();
                        } else {
                            i.getAndIncrement();
                        }
                    }
                });
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) return;
            if (item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName() != null) {
                    String display = ChatColor.stripColor(Color.translate(item.getItemMeta().getDisplayName()));
                    if (display.contains("Cancel")) {
                        new GrantMainMenu(this.player, this.document).open(player);
                    } else if (display.contains("Global")) {
                        new GrantSelectDurationMenu(this.player, this.document, rank, "global").open(player);
                    } else {
                        new GrantSelectDurationMenu(this.player, this.document, rank, display).open(player);
                    }
                }
            }
        }
    }
}