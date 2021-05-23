package com.solexgames.core.menu.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.Menu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class SettingsMenu extends Menu {

    private final Player player;

    @Override
    public String getTitle(Player player) {
        return "Settings ";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttonMap = new HashMap<>();
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);

        buttonMap.put(0, new ItemBuilder(XMaterial.PAPER.parseMaterial())
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Global Chat")
                .addLore(
                        "",
                        ChatColor.GRAY + "Would you like to be",
                        ChatColor.GRAY + "able to view global",
                        ChatColor.GRAY + "chat?",
                        " ",
                        (potPlayer.isCanSeeGlobalChat() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fEnabled",
                        (!potPlayer.isCanSeeGlobalChat() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fDisabled"
                )
                .toUpdatingButton((player1, clickType) -> potPlayer.setCanSeeGlobalChat(!potPlayer.isCanSeeGlobalChat()))
        );

        buttonMap.put(1, new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Server Tips")
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
                .toUpdatingButton((player1, clickType) -> potPlayer.setCanSeeTips(!potPlayer.isCanSeeTips()))
        );

        buttonMap.put(2, new ItemBuilder(XMaterial.EMERALD.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Receive DMs")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addLore(
                        "",
                        ChatColor.GRAY + "Would you like to be",
                        ChatColor.GRAY + "able to receive direct",
                        ChatColor.GRAY + "messages from other",
                        ChatColor.GRAY + "online players?",
                        " ",
                        (potPlayer.isCanReceiveDms() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fEnabled",
                        (!potPlayer.isCanReceiveDms() ? ChatColor.GREEN + ChatColor.BOLD.toString() + "■ " : ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "■ ") + "&fDisabled"
                )
                .toUpdatingButton((player1, clickType) -> potPlayer.setCanReceiveDms(!potPlayer.isCanReceiveDms()))
        );

        buttonMap.put(3, new ItemBuilder(XMaterial.JUKEBOX.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "DMs Sounds")
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
                .toUpdatingButton((player1, clickType) -> potPlayer.setCanReceiveDmsSounds(!potPlayer.isCanReceiveDmsSounds()))
        );

        if (player.hasPermission("scandium.staff")) {
            buttonMap.put(4, new ItemBuilder(XMaterial.BLAZE_POWDER.parseMaterial())
                    .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Staff Messages")
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
                    .toUpdatingButton((player1, clickType) -> potPlayer.setCanSeeStaffMessages(!potPlayer.isCanSeeStaffMessages()))
            );
        }

        final AtomicInteger integer = new AtomicInteger(buttonMap.size());

        CorePlugin.getInstance().getSettingsList().forEach(settings -> settings.getButtons(player).forEach(button -> buttonMap.put(integer.getAndIncrement(), button)));

        return buttonMap;
    }
}
