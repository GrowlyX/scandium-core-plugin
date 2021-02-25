package com.solexgames.core.menu.extend.color;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class NameColorSelectMenu extends AbstractInventoryMenu {

    private Player player;

    public NameColorSelectMenu(Player player) {
        super("Custom Color", 9*6);
        this.player = player;
        this.update();
    }

    public void update() {
        int i = 10;
        for (ChatColor chatColor : ChatColor.values()) {
            if (chatColor != ChatColor.MAGIC) {
                if (chatColor != ChatColor.STRIKETHROUGH) {
                    if (chatColor != ChatColor.RESET) {
                        if (chatColor != ChatColor.UNDERLINE) {
                            this.inventory.setItem(i, new ItemBuilder(Material.NAME_TAG)
                                    .setDisplayName(chatColor + chatColor.name().replace("_", " "))
                                    .addLore(Arrays.asList(
                                            "&b&m------------------------------------",
                                            "&eClick to select the " + chatColor + chatColor.name() + " &ecolor!",
                                            "&b&m------------------------------------"
                                    ))
                                    .create()
                            );

                            if ((i == 16) || (i == 25)) {
                                i++;
                                i++;
                                i++;
                            } else i++;
                        }
                    }
                }
            }
        }

        this.inventory.setItem(49, new ItemBuilder(Material.BED).setDisplayName("&cReset Chat Color").addLore(Arrays.asList("", "&7Click to reset your", "&7current chat color!")).create());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

            if (event.getRawSlot() == 49) {
                potPlayer.setCustomColor(null);
                player.sendMessage(Color.translate("&aReset your chat color to default!"));
                return;
            }
            if (item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName() != null) {
                    String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                    ChatColor chatColor = ChatColor.valueOf(display.replace(" ", "_"));

                    if (potPlayer.isHasVoted()) {
                        potPlayer.setCustomColor(chatColor);
                        player.sendMessage(Color.translate("&aChanged your chat color to " + chatColor + chatColor.name() + "&a!"));
                    } else {
                        player.sendMessage(Color.translate("&cYou cannot change chat colors!"));
                        player.sendMessage(Color.translate("&cTo be able to chat chat colors, vote for us on NameMC!"));
                        player.sendMessage(Color.translate("&chttps://namemc.com/" + CorePlugin.getInstance().getServerManager().getNetwork().getWebsiteLink() + "/"));
                    }
                }
            }
        }
    }
}
