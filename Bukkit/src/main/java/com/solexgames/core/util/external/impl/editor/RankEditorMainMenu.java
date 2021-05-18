package com.solexgames.core.util.external.impl.editor;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.WoolUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.impl.grant.GrantDurationPaginatedMenu;
import com.solexgames.core.util.external.impl.grant.GrantScopePaginatedMenu;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 5/18/2021
 */

public class RankEditorMainMenu extends PaginatedMenu {

    public RankEditorMainMenu() {
        super(45);
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(3, new ItemBuilder(XMaterial.NETHER_STAR.parseMaterial())
                .setDisplayName(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Rank Editor")
                .addLore(
                        "&7Welcome to the rank editor",
                        "&7main menu.",
                        "",
                        "&7To get started, please select",
                        "&7one of the ranks listed below.",
                        "",
                        Color.SECONDARY_COLOR + "Need to make a new rank?",
                        "&7No problem! Click the redstone",
                        "&7comparator to get started!"
                )
                .toButton());

        buttons.put(5, new ItemBuilder(XMaterial.COMPARATOR.parseMaterial())
                .setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Rank Creator")
                .addLore(
                        "&7Need to create a new rank",
                        "&7quick and easily? Click this",
                        "&7item to get started!",
                        "",
                        "&e[Click to create new rank]"
                )
                .toButton((player1, clickType) -> player1.sendMessage(ChatColor.RED + "This feature's not available at the moment.")));

        return buttons;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Select a Rank";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();
        final AtomicInteger i = new AtomicInteger(0);

        this.getSortedRanks().forEach(rank -> buttons.put(i.getAndIncrement(), new RankButton(rank)));

        return buttons;
    }

    private List<Rank> getSortedRanks() {
        return Rank.getRanks().stream().sorted(Comparator.comparingInt(Rank::getWeight).reversed()).collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    private static class RankButton extends Button {

        private final Rank rank;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(XMaterial.RED_WOOL.parseMaterial(), ((this.rank.getColor() != null) ? (ChatColor.getByChar(Color.translate(this.rank.getColor().replace("&", "").replace("§", ""))) != null) ? WoolUtil.getByColor(ChatColor.getByChar(Color.translate(this.rank.getColor().replace("&", "").replace("§", "")))) : 0 : 0))
                    .addLore("&e[Click to edit this rank]")
                    .setDisplayName(this.rank.getColor() + this.rank.getItalic() + this.rank.getName())
                    .create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new RankEditorEditMenu(this.rank).openMenu(player);
        }
    }
}
