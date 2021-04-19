package com.solexgames.core.util.external.pagination.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class PrefixViewPaginatedMenu extends PaginatedMenu {

    private final Player player;

    public PrefixViewPaginatedMenu(Player player) {
        super(27);
        this.player = player;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_BED.parseMaterial()).setDisplayName("&cReset Prefix").addLore(Arrays.asList("&7Click to reset your", "&7current applied prefix!")).create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                potPlayer.setAppliedPrefix(null);

                player.sendMessage(ChatColor.GREEN + Color.translate("Reset your prefix to default!"));
                player.closeInventory();
            }
        });

        return buttons;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Prefixes";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();
        final AtomicInteger i = new AtomicInteger(0);
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Prefix.getPrefixes().forEach(prefix -> buttons.put(i.getAndIncrement(), new PrefixButton(potPlayer, prefix)));

        return buttons;
    }

    @AllArgsConstructor
    private static class PrefixButton extends Button {

        private final PotPlayer potPlayer;
        private final Prefix prefix;

        @Override
        public ItemStack getButtonItem(Player player) {
            ArrayList<String> lore = new ArrayList<>();
            boolean hasPrefix = this.potPlayer.getAllPrefixes().contains(this.prefix.getName());

            lore.add("  ");

            if (hasPrefix) {
                lore.add("&7You own this prefix and it");
                lore.add("&7can be enabled at any time.");
            } else {
                lore.add("&7This prefix is currently");
                lore.add("&7locked as you don't own it.");
            }

            lore.add("  ");
            lore.add("&7Appears in chat as:");
            lore.add(this.prefix.getPrefix());
            lore.add("  ");
            lore.add((hasPrefix ? "&aClick to equip this prefix." : "&cYou don't own this prefix."));

            return new ItemBuilder((this.potPlayer.getAllPrefixes().contains(this.prefix.getName()) ? XMaterial.LIME_DYE.parseMaterial() : XMaterial.RED_DYE.parseMaterial()), (this.potPlayer.getAllPrefixes().contains(this.prefix.getName()) ? 10 : 1))
                    .setDisplayName((hasPrefix ? "&e" : "&c") + this.prefix.getName())
                    .addLore(Color.translate(lore))
                    .create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (this.potPlayer.getAllPrefixes().contains(this.prefix.getName())) {
                this.potPlayer.setAppliedPrefix(this.prefix);
                player.sendMessage(Color.SECONDARY_COLOR + "You've updated your chat prefix to " + Color.MAIN_COLOR + this.prefix.getName() + ChatColor.GRAY + " (" + this.prefix.getPrefix() + ChatColor.GRAY + ")" + Color.SECONDARY_COLOR + ".");
            } else {
                player.sendMessage(ChatColor.RED + ("You do not own this prefix!"));
                player.sendMessage(ChatColor.RED + ("You can purchase this prefix at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + "!"));
            }

            player.closeInventory();
        }
    }
}