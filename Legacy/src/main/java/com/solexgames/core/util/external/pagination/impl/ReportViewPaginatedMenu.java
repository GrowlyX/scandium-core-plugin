package com.solexgames.core.util.external.pagination.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.player.report.Report;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportViewPaginatedMenu extends PaginatedMenu {

    private boolean onlyResolved = false;

    public ReportViewPaginatedMenu() {
        super(27);
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.EMERALD.parseMaterial()).setDisplayName("&a&lReport View").addLore("&7Would you like to see", "&7all reports or only resolved", "&7reports?", "", "&7Current: &6" + (onlyResolved ? "Only Resolved": "All")).create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                onlyResolved = !onlyResolved;
                setUpdateAfterClick(true);
            }
        });

        return buttons;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Reports";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        AtomicInteger i = new AtomicInteger(0);

        if (onlyResolved) {
            CorePlugin.getInstance().getReportManager().getReports().stream().filter(Report::isResolved).forEach(report -> buttons.put(i.getAndIncrement(), new ReportButton(report)));
        } else {
            CorePlugin.getInstance().getReportManager().getReports().forEach(report -> buttons.put(i.getAndIncrement(), new ReportButton(report)));
        }

        return buttons;
    }

    @AllArgsConstructor
    public static class ReportButton extends Button {

        private final Report report;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER).create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
        }
    }
}
