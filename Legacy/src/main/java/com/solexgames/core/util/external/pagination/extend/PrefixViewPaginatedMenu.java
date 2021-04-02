package com.solexgames.core.util.external.pagination.extend;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
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
        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_BED.parseMaterial()).setDisplayName("&cReset Prefix").addLore(Arrays.asList("&7Click to reset your", "&7current applied prefix!")).create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                potPlayer.setAppliedPrefix(null);
                player.sendMessage(Color.translate("&aReset your prefix to default!"));
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

            buttons.put(i.getAndIncrement(), new Button() {

                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder((potPlayer.getAllPrefixes().contains(prefix.getName()) ? XMaterial.LIME_DYE.parseMaterial() : XMaterial.RED_DYE.parseMaterial()), (potPlayer.getAllPrefixes().contains(prefix.getName()) ? 10 : 1))
                            .setDisplayName((hasPrefix ? "&e" : "&c") + prefix.getName())
                            .addLore(Color.translate(lore))
                            .create();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    Prefix prefix = Prefix.getByName(ChatColor.stripColor(getButtonItem(player).getItemMeta().getDisplayName()));
                    if (potPlayer.getAllPrefixes().contains(prefix.getName())) {
                        potPlayer.setAppliedPrefix(prefix);
                        player.sendMessage(Color.translate("&aYou've updated your prefix to &6" + prefix.getName() + "&a!"));
                    } else {
                        player.sendMessage(Color.translate("&cYou do not own this prefix!"));
                        player.sendMessage(Color.translate("&cYou can purchase this prefix at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + "!"));
                    }
                    player.closeInventory();
                }
            });
        });

        return buttons;
    }
}
