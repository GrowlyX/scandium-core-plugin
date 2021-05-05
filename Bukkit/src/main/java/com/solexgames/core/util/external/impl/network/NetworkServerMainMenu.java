package com.solexgames.core.util.external.impl.network;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkServerMainMenu extends PaginatedMenu {

    public NetworkServerMainMenu() {
        super(18);
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "All available Servers";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final HashMap<Integer, Button> buttonHashMap = new HashMap<>();
        final AtomicInteger atomicInteger = new AtomicInteger();

        CorePlugin.getInstance().getServerManager().getNetworkServers().forEach(server -> buttonHashMap.put(atomicInteger.getAndIncrement(), new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.GRAY_WOOL.parseMaterial())
                        .setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + server.getServerName())
                        .addLore(
                                ChatColor.GRAY + "Status: " + server.getServerStatus().getServerStatusFancyString(),
                                "",
                                ChatColor.YELLOW + "[Click to view more information]"
                        )
                        .setDurability(7)
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new NetworkServerInfoMenu(server).openMenu(player);
            }
        }));

        return buttonHashMap;
    }
}
