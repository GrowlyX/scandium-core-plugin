package com.solexgames.core.util.external.pagination.extend;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.extend.grant.GrantSelectDurationMenu;
import com.solexgames.core.menu.extend.grant.scope.GrantScopeSelectMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.WoolUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class PrefixViewPaginatedMenu extends PaginatedMenu {

    private final Player player;

    public PrefixViewPaginatedMenu(Player player) {
        this.player = player;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Prefixes";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        AtomicInteger i = new AtomicInteger(0);
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Prefix.getPrefixes().forEach(prefix -> {
            ArrayList<String> lore = new ArrayList<>();

            boolean hasPrefix = potPlayer.getAllPrefixes().contains(prefix.getName());

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
            lore.add(prefix.getPrefix());
            lore.add("  ");
            lore.add((hasPrefix ? "&aClick to equip this prefix." : "&cYou don't own this prefix."));

            buttons.put(i.get(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(XMaterial.INK_SAC.parseMaterial(), (potPlayer.getAllPrefixes().contains(prefix.getName()) ? 10 : 1))
                            .setDisplayName((hasPrefix ? "&e" : "&c") + prefix.getName())
                            .addLore(Color.translate(lore))
                            .create();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    Prefix prefix = Prefix.getByName(ChatColor.stripColor(getButtonItem(player).getItemMeta().getDisplayName()));
                    if (potPlayer.getAllPrefixes().contains(prefix.getName())) {
                        potPlayer.setAppliedPrefix(prefix);
                        player.sendMessage(Color.translate("&aYou have updated your prefix to &6" + prefix.getName() + "&a!"));
                    } else {
                        player.sendMessage(Color.translate("&cYou do not own this prefix!"));
                        player.sendMessage(Color.translate("&cYou can purchase this prefix at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + "!"));
                    }
                    player.closeInventory();
                }
            });

            i.getAndIncrement();
        });

        return buttons;
    }
}
