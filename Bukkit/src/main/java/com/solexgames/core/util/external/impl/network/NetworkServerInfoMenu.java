package com.solexgames.core.util.external.impl.network;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.Menu;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class NetworkServerInfoMenu extends Menu {

    private final NetworkServer networkServer;

    @Override
    public String getTitle(Player player) {
        return Color.MAIN_COLOR + this.networkServer.getServerName() + ChatColor.DARK_GRAY + " Information";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final HashMap<Integer, Button> buttonHashMap = new HashMap<>();

        buttonHashMap.put(0, new ItemBuilder(XMaterial.MELON_SLICE.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + this.networkServer.getServerName())
                .addLore(ChatColor.GRAY + "Viewing in-depth details of this server.")
                .toButton()
        );

        buttonHashMap.put(2, new ItemBuilder(XMaterial.STICKY_PISTON.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "TPS")
                .addLore(
                        ChatColor.GRAY + "Average: " + Color.SECONDARY_COLOR + this.networkServer.getTicksPerSecond(),
                        ChatColor.GRAY + "Simple: " + Color.SECONDARY_COLOR + this.networkServer.getTicksPerSecondSimplified()
                )
                .toButton()
        );

        buttonHashMap.put(3, new ItemBuilder(XMaterial.REDSTONE_LAMP.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Server Type")
                .addLore(
                        ChatColor.GRAY + "Type: " + Color.SECONDARY_COLOR + this.networkServer.getServerType().getServerTypeString()
                )
                .toButton()
        );

        buttonHashMap.put(4, new ItemBuilder(XMaterial.EMERALD.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Server Status")
                .addLore(
                        ChatColor.GRAY + "Status: " + Color.SECONDARY_COLOR + this.networkServer.getServerStatus().getServerStatusFancyString()
                )
                .toButton()
        );

        buttonHashMap.put(5, new ItemBuilder(XMaterial.EGG.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Online Players")
                .addLore(
                        ChatColor.GRAY + "Online: " + Color.SECONDARY_COLOR + this.networkServer.getOnlinePlayers()
                )
                .toButton()
        );

        buttonHashMap.put(6, new ItemBuilder(XMaterial.BARRIER.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Other Information")
                .addLore(
                        ChatColor.GRAY + "Whitelist: " + Color.SECONDARY_COLOR + this.networkServer.isWhitelistEnabled(),
                        ChatColor.GRAY + "Last Update: " + ChatColor.WHITE + (System.currentTimeMillis() - this.networkServer.getLastUpdate()) + "ms ago. " + Color.SECONDARY_COLOR + "(" + CorePlugin.FORMAT.format(new Date(this.networkServer.getLastUpdate()) + ")")
                )
                .toButton()
        );

        buttonHashMap.put(8, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_BED.parseMaterial())
                        .setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Return Home")
                        .addLore(
                                ChatColor.GRAY + "Return to the main server",
                                ChatColor.GRAY + "selection list.",
                                "",
                                ChatColor.YELLOW + "[Click to return home]"
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new NetworkServerMainMenu().openMenu(player);
            }
        });

        return buttonHashMap;
    }
}
