package com.solexgames.core.menu.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class SettingsMenu extends AbstractInventoryMenu {

    private Player player;

    public SettingsMenu(Player player) {
        super("Settings", 9);
        this.player = player;
        this.update();
    }

    public void update() {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(2, new ItemBuilder(XMaterial.PAPER.parseMaterial())
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .setDisplayName(serverType.getMainColor() + ChatColor.BOLD.toString() + "Global Chat")
                .addLore(
                        "",
                        ChatColor.GRAY + "Would you like to be",
                        ChatColor.GRAY + "able to view global",
                        ChatColor.GRAY + "chat?",
                        " ",
                        (potPlayer.isCanSeeGlobalChat() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fEnabled",
                        (!potPlayer.isCanSeeGlobalChat() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fDisabled"
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE.parseMaterial())
                .setDisplayName(serverType.getMainColor() + ChatColor.BOLD.toString() + "Server Tips")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addLore(
                        "",
                        ChatColor.GRAY + "Would you like to be",
                        ChatColor.GRAY + "able to view server",
                        ChatColor.GRAY + "tips?",
                        " ",
                        (potPlayer.isCanSeeTips() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fEnabled",
                        (!potPlayer.isCanSeeTips() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fDisabled"
                )
                .create()
        );
        this.inventory.setItem(4, new ItemBuilder(XMaterial.EMERALD.parseMaterial())
                .setDisplayName(serverType.getMainColor() + ChatColor.BOLD.toString() + "Receive DMs")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addLore(
                        "",
                        ChatColor.GRAY + "Would you like to be",
                        ChatColor.GRAY + "able to receive player",
                        ChatColor.GRAY + "dms?",
                        " ",
                        (potPlayer.isCanReceiveDms() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fEnabled",
                        (!potPlayer.isCanReceiveDms() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fDisabled"
                )
                .create()
        );
        this.inventory.setItem(5, new ItemBuilder(XMaterial.JUKEBOX.parseMaterial())
                .setDisplayName(serverType.getMainColor() + ChatColor.BOLD.toString() + "DMs Sounds")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addLore(
                        "",
                        ChatColor.GRAY + "Would you like to be",
                        ChatColor.GRAY + "able to receive dm",
                        ChatColor.GRAY + "sounds?",
                        " ",
                        (potPlayer.isCanReceiveDmsSounds() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fEnabled",
                        (!potPlayer.isCanReceiveDmsSounds() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fDisabled"
                )
                .create()
        );
        if (player.hasPermission("scandium.staff")) {
            this.inventory.setItem(6, new ItemBuilder(XMaterial.BLAZE_POWDER.parseMaterial())
                    .setDisplayName(serverType.getMainColor() + ChatColor.BOLD.toString() + "Staff Messages")
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                    .addLore(
                            "",
                            ChatColor.GRAY + "Would you like to be",
                            ChatColor.GRAY + "able to receive staff",
                            ChatColor.GRAY + "messages?",
                            " ",
                            (potPlayer.isCanSeeStaffMessages() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fEnabled",
                            (!potPlayer.isCanSeeStaffMessages() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fDisabled"
                    )
                    .create()
            );
        } else {
            this.inventory.setItem(6, new ItemBuilder(XMaterial.BLAZE_POWDER.parseMaterial())
                    .setDisplayName(serverType.getMainColor() + ChatColor.BOLD.toString() + "Global Broadcasts")
                    .addLore(
                            "",
                            ChatColor.GRAY + "Would you like to be",
                            ChatColor.GRAY + "able to receive global",
                            ChatColor.GRAY + "announcements?",
                            " ",
                            (potPlayer.isCanSeeBroadcasts() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fEnabled",
                            (!potPlayer.isCanSeeBroadcasts() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fDisabled"
                    )
                    .create()
            );
        }
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

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            switch (event.getRawSlot()) {
                case 2:
                    potPlayer.setCanSeeGlobalChat(!potPlayer.isCanSeeGlobalChat());
                    break;
                case 3:
                    potPlayer.setCanSeeTips(!potPlayer.isCanSeeTips());
                    break;
                case 4:
                    potPlayer.setCanReceiveDms(!potPlayer.isCanReceiveDms());
                    break;
                case 5:
                    potPlayer.setCanReceiveDmsSounds(!potPlayer.isCanReceiveDmsSounds());
                    break;
                case 6:
                    if (potPlayer.getPlayer().hasPermission("scandium.staff")) {
                        potPlayer.setCanSeeStaffMessages(!potPlayer.isCanSeeStaffMessages());
                    } else {
                        potPlayer.setCanSeeBroadcasts(!potPlayer.isCanSeeBroadcasts());
                    }
                    break;
            }

            this.update();
        }
    }
}
