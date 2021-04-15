package com.solexgames.core.util.external.pagination.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.DateUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GrantDurationPaginatedMenu extends PaginatedMenu {

    private final Player player;
    private final Document document;
    private final Rank rank;
    private final String scope;

    public GrantDurationPaginatedMenu(Player player, Document document, Rank rank, String scope) {
        super(9);

        this.player = player;
        this.document = document;
        this.rank = rank;
        this.scope = scope;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        AtomicInteger atomicInteger = new AtomicInteger();

        buttonMap.put(atomicInteger.getAndIncrement(), new DurationButton(XMaterial.WHITE_WOOL, 0, "1d", "1 Day", this));
        buttonMap.put(atomicInteger.getAndIncrement(), new DurationButton(XMaterial.ORANGE_WOOL, 1, "1w", "1 Week", this));
        buttonMap.put(atomicInteger.getAndIncrement(), new DurationButton(XMaterial.PINK_WOOL, 2, "1m", "1 Month", this));
        buttonMap.put(atomicInteger.getAndIncrement(), new DurationButton(XMaterial.LIGHT_BLUE_WOOL, 3, "3m", "3 Months", this));
        buttonMap.put(atomicInteger.getAndIncrement(), new DurationButton(XMaterial.YELLOW_WOOL, 4, "6m", "6 Months", this));
        buttonMap.put(atomicInteger.getAndIncrement(), new DurationButton(XMaterial.GREEN_WOOL, 5, "1y", "1 Year", this));

        return buttonMap;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Grant time for: " + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name"));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        buttonMap.put(2, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseMaterial())
                        .setDurability(5)
                        .setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Custom Duration")
                        .addLore(
                                ChatColor.GRAY + "Click to choose a custom",
                                ChatColor.GRAY + "duration for your grant!"
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                potPlayer.setGrantDurationEditing(true);
                potPlayer.setGrantDurationTarget(document);
                potPlayer.setGrantDurationPerm(false);
                potPlayer.setGrantDurationRank(rank);
                potPlayer.setGrantDurationScope(scope);

                player.sendMessage(Color.translate("  "));
                player.sendMessage(ChatColor.GREEN + Color.translate("Type in a custom duration in chat!"));
                player.sendMessage(Color.translate("&7&oUse 'cancel' to cancel this process, or use 'perm' to set it as a permanent punishment!"));
                player.sendMessage(Color.translate("  "));

                player.closeInventory();
            }
        });

        buttonMap.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_BED.parseMaterial())
                        .setDurability(14)
                        .setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Return Back")
                        .addLore(
                                ChatColor.GRAY + "Click to return to the",
                                ChatColor.GRAY + "previous menu!"
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new GrantMainPaginatedMenu(document, player);
            }
        });

        buttonMap.put(6, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_TERRACOTTA.parseMaterial())
                        .setDurability(14)
                        .setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Permanent")
                        .addLore(
                                ChatColor.GRAY + "Click to select the perm",
                                ChatColor.GRAY + "duration for this grant."
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new GrantSelectReasonMenu(player, document, -1L, rank, true, scope).open(player);
            }
        });

        return buttonMap;
    }

    @AllArgsConstructor
    public static class DurationButton extends Button {

        private final XMaterial material;
        private final int data;
        private final String dateDiff;
        private final String fancyName;

        private final GrantDurationPaginatedMenu menuData;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(this.material.parseMaterial())
                    .setDisplayName(Color.MAIN_COLOR + this.fancyName)
                    .addLore(ChatColor.GRAY + "Click to choose this duration.")
                    .setDurability(this.data)
                    .create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new GrantReasonPaginatedMenu(menuData.getPlayer(), menuData.getDocument(), System.currentTimeMillis() - DateUtil.parseDateDiff(this.dateDiff, false), menuData.getRank(), false, menuData.getScope()).openMenu(player);
        }
    }
}
