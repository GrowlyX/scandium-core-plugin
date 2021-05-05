package com.solexgames.core.util.external.impl.grant;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
                        .setDisplayName("&a&lGlobal Scope")
                        .addLore(
                                "&7Set the grant scope as",
                                "&7global.",
                                "",
                                ChatColor.YELLOW + "[Click to select]"
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new GrantDurationPaginatedMenu(player, document, rank, "global").openMenu(player);
            }
        });

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.LIGHT_BLUE_TERRACOTTA.parseMaterial(), 3)
                        .setDisplayName("&b&lScope Selection")
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
                        .setDisplayName("&c&lReturn to Main")
                        .addLore(
                                "&7Cancel this grant and",
                                "&7return to the main menu.",
                                "",
                                ChatColor.YELLOW + "[Click to return]"
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
        return "Select grant scope for: " + (Bukkit.getPlayerExact(document.getString("name")) != null ? Bukkit.getPlayerExact(document.getString("name")).getDisplayName() : document.getString("name"));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final HashMap<Integer, Button> buttonMap = new HashMap<>();
        final AtomicInteger i = new AtomicInteger(0);

        CorePlugin.getInstance().getServerManager().getNetworkServers().stream()
                .filter(Objects::nonNull)
                .forEach(server -> buttonMap.put(i.getAndIncrement(), new ScopeButton(server)));

        return buttonMap;
    }

    @RequiredArgsConstructor
    private class ScopeButton extends Button {

        private final NetworkServer server;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(XMaterial.PAPER.parseMaterial())
                    .setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + this.server.getServerName())
                    .addLore(
                            "&7Click to select this server",
                            "&7for the grant scope."
                    )
                    .create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new GrantDurationPaginatedMenu(player, document, rank, this.server.getServerName()).openMenu(player);
        }
    }
}
