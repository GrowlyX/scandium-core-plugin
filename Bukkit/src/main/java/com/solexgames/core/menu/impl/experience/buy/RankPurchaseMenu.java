package com.solexgames.core.menu.impl.experience.buy;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RankPurchaseMenu extends PaginatedMenu {

    public RankPurchaseMenu() {
        super(27);
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Rank Shop";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttonMap = new HashMap<>();
        final AtomicInteger integer = new AtomicInteger(0);
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (Prefix.getPrefixes().stream().noneMatch(Prefix::isPurchasable)) {
            for (int i = 0; i < 8; i++) {
                buttonMap.put(i, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
                                .setDisplayName(ChatColor.RED + "No purchasable ranks!")
                                .create();
                    }
                });
            }

            return buttonMap;
        }

        /*Rank.getRanks().stream().filter(Rank::isPurchasable).forEach(prefix -> buttonMap.put(integer.getAndIncrement(), new Button() {

            @Override
            public ItemStack getButtonItem(Player player) {
                final boolean ownsPrefix = potPlayer.getAllPrefixes().contains(prefix.getName());
                final List<String> lore = new ArrayList<>();

                lore.add("&7This rank costs:");
                lore.add("&e500 Experience");
                lore.add(" ");
                lore.add("&7This rank will view as:");
                lore.add(prefix.getPrefix());
                lore.add(" ");

                if (ownsPrefix) {
                    lore.add("&cYou already own this prefix!");
                } else {
                    if (potPlayer.getExperience() >= 500) {
                        lore.add("&aClick to purchase this prefix");
                        lore.add("&afor 500 Experience!");
                    } else {
                        lore.add("&cYou need " + (500 - potPlayer.getExperience()) + " more");
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
        }));*/

        return buttonMap;
    }
}
