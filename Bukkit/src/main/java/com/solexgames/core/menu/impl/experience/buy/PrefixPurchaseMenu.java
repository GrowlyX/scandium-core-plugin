package com.solexgames.core.menu.impl.experience.buy;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.impl.experience.ExperienceShopMainMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.Menu;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PrefixPurchaseMenu extends PaginatedMenu {

    public PrefixPurchaseMenu() {
        super(27);
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_BED.parseMaterial())
                        .setDisplayName("&cReturn")
                        .addLore(
                                "&7Click to return to the",
                                "&7main shop menu!"
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                setClosedByMenu(true);

                new ExperienceShopMainMenu().open(player);
            }
        });

        return buttons;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Prefix Shop";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttonMap = new HashMap<>();
        final AtomicInteger integer = new AtomicInteger(0);
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (Prefix.getPrefixes().stream().noneMatch(Prefix::isPurchasable)) {
            for (int i = 0; i <= 8; i++) {
                buttonMap.put(i, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
                                .setDurability(1)
                                .setDisplayName(ChatColor.RED + "No purchasable prefixes!")
                                .create();
                    }
                });
            }

            return buttonMap;
        }

        Prefix.getPrefixes().stream().filter(Prefix::isPurchasable).forEach(prefix -> buttonMap.put(integer.getAndIncrement(), new Button() {

            @Override
            public ItemStack getButtonItem(Player player) {
                final boolean ownsPrefix = potPlayer.getAllPrefixes().contains(prefix.getName());
                final List<String> lore = new ArrayList<>();

                lore.add("&7This prefix costs:");
                lore.add("&e500 Experience");
                lore.add(" ");
                lore.add("&7This prefix will view as:");
                lore.add(prefix.getPrefix());
                lore.add(" ");

                if (ownsPrefix) {
                    lore.add("&cYou already own this prefix!");
                } else {
                    if (potPlayer.getExperience() >= 500) {
                        lore.add("&aClick to purchase this prefix");
                        lore.add("&afor 500 Experience!");
                    } else {
                        lore.add("&cYou need " + ChatColor.BOLD.toString() + (500 - potPlayer.getExperience()) + ChatColor.RED + " more");
                        lore.add("&cexperience to purchase this!");
                    }
                }

                return new ItemBuilder(XMaterial.NAME_TAG.parseMaterial())
                        .setDisplayName((ownsPrefix ? ChatColor.GREEN.toString() : ChatColor.RED.toString()) + prefix.getName())
                        .addLore(lore)
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                final String display = ChatColor.stripColor(this.getButtonItem(player).getItemMeta().getDisplayName());
                final boolean ownsPrefix = potPlayer.getAllPrefixes().contains(prefix.getName());
                final Prefix prefix = Prefix.getByName(display);

                if (ownsPrefix) {
                    player.sendMessage(ChatColor.RED + "You already own this prefix!");
                } else {
                    if (potPlayer.getExperience() >= 500) {
                        potPlayer.setExperience(potPlayer.getExperience() - 500);
                        player.sendMessage(ChatColor.GREEN + "You've successfully purchased the " + ChatColor.GOLD + prefix.getName() + ChatColor.GRAY + " (" + Color.translate(prefix.getPrefix()) + ChatColor.GRAY + ")" + ChatColor.GREEN + " prefix!");

                        potPlayer.setAppliedPrefix(prefix);
                        potPlayer.getAllPrefixes().add(prefix.getName());
                    } else {
                        player.sendMessage(ChatColor.RED + "You cannot afford this prefix!");
                    }
                }

                player.closeInventory();
            }
        }));

        return buttonMap;
    }
}
