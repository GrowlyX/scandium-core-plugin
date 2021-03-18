package com.solexgames.core.util.external.pagination.extend;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.extend.grant.GrantSelectDurationMenu;
import com.solexgames.core.menu.extend.grant.remove.GrantRemoveConfirmMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GrantScopePaginatedMenu extends PaginatedMenu {

    private final Player player;
    private final Document document;
    private final Rank rank;

    public GrantScopePaginatedMenu(Player player, Document document, Rank rank) {
        super(9);

        this.rank = rank;
        this.player = player;
        this.document = document;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(3, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.LIME_TERRACOTTA.parseMaterial(), 5)
                        .setDisplayName("&aGlobal Scope")
                        .addLore(
                                "&7Click to select the global",
                                "&7scope for this grant."
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new GrantSelectDurationMenu(player, document, rank, "global").open(player);
            }
        });

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.LIGHT_BLUE_TERRACOTTA.parseMaterial(), 3)
                        .setDisplayName("&bScope Selection")
                        .addLore(
                                "&7Use the buttons below to",
                                "&7select a server for this",
                                "&7grant to be applied on!"
                        )
                        .create();
            }
        });

        buttons.put(5, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_TERRACOTTA.parseMaterial(), 14)
                        .setDisplayName("&cReturn to Main")
                        .addLore(
                                "&7Click to cancel this grant",
                                "&7and return to the main menu."
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new GrantMainPaginatedMenu(document, player).openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Select grant scope for: " + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name"));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> buttonMap = new HashMap<>();
        AtomicInteger i = new AtomicInteger(0);

        CorePlugin.getInstance().getServerManager().getNetworkServers().stream()
                .filter(Objects::nonNull)
                .forEach(server -> buttonMap.put(i.getAndIncrement(), new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(XMaterial.PAPER.parseMaterial())
                                .setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + server.getServerName())
                                .addLore(
                                        "&7Click to select this server",
                                        "&7for the grant scope."
                                )
                                .create();
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        String display = ChatColor.stripColor(getButtonItem(player).getItemMeta().getDisplayName());

                        new GrantSelectDurationMenu(player, document, rank, display).open(player);
                    }
                }));

        return buttonMap;
    }
}
