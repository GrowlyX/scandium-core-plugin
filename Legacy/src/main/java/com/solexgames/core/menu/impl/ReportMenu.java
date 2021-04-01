package com.solexgames.core.menu.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ReportType;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 4/1/2021
 */

@Getter
public class ReportMenu extends AbstractInventoryMenu {

    private final Player player;
    private final Player target;

    public ReportMenu(Player player, Player target) {
        super("Report", 9);
        this.player = player;
        this.target = target;
        this.update();
    }

    public void update() {
        AtomicInteger integer = new AtomicInteger();
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        Arrays.asList(ReportType.values()).forEach(reportType -> this.inventory.setItem(integer.getAndIncrement(), new ItemBuilder(reportType.getXMaterial().parseMaterial())
                .setDisplayName(serverType.getMainColor() + reportType.getName())
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addLore(reportType.getExamples().stream().map(s -> ChatColor.GRAY + " * " + serverType.getSecondaryColor() + s).collect(Collectors.toList()))
                .setDurability(reportType.getDurability())
                .create()
        ));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;

            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);
            ReportType reportType = Arrays.stream(ReportType.values()).filter(reportType1 -> reportType1.getName().equalsIgnoreCase(ChatColor.stripColor(item.getItemMeta().getDisplayName()))).findFirst().orElse(null);

            if (reportType != null && potPlayer.isCanReport()) {
                RedisUtil.writeAsync(RedisUtil.onReport(this.player, this.target, reportType.getName()));
                CorePlugin.getInstance().getDiscordManager().sendReport(player, target, reportType.getName());

                potPlayer.setCanReport(false);

                Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
                    PotPlayer updatedPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);

                    if (updatedPotPlayer != null) {
                        updatedPotPlayer.setCanReport(true);
                    }
                }, 60 * 20L);

                this.player.sendMessage(ChatColor.GREEN + "Your report has been sent to all online staff!");
            } else {
                this.player.sendMessage(ChatColor.RED + "You cannot perform this action right now.");
            }

            this.player.closeInventory();
        }
    }
}
