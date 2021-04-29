package com.solexgames.core.util.external.pagination.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.WoolUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
public class NameColorSelectMenu extends PaginatedMenu {

    public NameColorSelectMenu() {
        super(18);
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        buttonMap.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_BED.parseMaterial())
                        .setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Reset Color")
                        .addLore(
                                ChatColor.GRAY + "Reset your current",
                                ChatColor.GRAY + "chat name color!",
                                "",
                                ChatColor.YELLOW + "[Click to reset]"
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                potPlayer.setCustomColor(null);
                player.sendMessage(ChatColor.GREEN + "Your custom name color has been reset!");

                player.closeInventory();
            }
        });

        return buttonMap;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Custom Colors";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        AtomicInteger atomicInteger = new AtomicInteger();

        Arrays.stream(ChatColor.values())
                .filter(ChatColor::isColor)
                .forEach(chatColor -> buttonMap.put(atomicInteger.getAndIncrement(), new ChatColorButton(chatColor, StringUtils.capitalize(chatColor.name().replace("_", " ").toLowerCase()))));

        return buttonMap;
    }

    @Getter
    @AllArgsConstructor
    private static class ChatColorButton extends Button {

        private final ChatColor chatColor;
        private final String chatColorFancyName;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(XMaterial.WHITE_WOOL.parseMaterial())
                    .setDisplayName(this.chatColor + this.chatColorFancyName)
                    .setDurability(WoolUtil.getByColor(this.chatColor))
                    .addLore(ChatColor.GRAY + "Click to select " + this.chatColor + this.chatColorFancyName + ChatColor.GRAY + "!")
                    .create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

            potPlayer.setCustomColor(this.chatColor);
            player.sendMessage(ChatColor.GREEN + "Modified your chat name color to " + this.chatColor + this.chatColorFancyName + ChatColor.GREEN + "!");

            player.closeInventory();
        }
    }
}
