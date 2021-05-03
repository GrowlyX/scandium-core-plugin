package com.solexgames.core.menu.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ReportType;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.player.report.Report;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import lombok.Getter;
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
        final AtomicInteger integer = new AtomicInteger();
        final ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        Arrays.asList(ReportType.values()).forEach(reportType -> this.inventory.setItem(integer.getAndIncrement(), new ItemBuilder(reportType.getXMaterial().parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + reportType.getName())
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addLore(reportType.getExamples().stream().map(s -> ChatColor.GRAY + " * " + Color.SECONDARY_COLOR + s).collect(Collectors.toList()))
                .setDurability(reportType.getDurability())
                .create()
        ));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        final Inventory topInventory = event.getView().getTopInventory();

        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            final ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;

            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);
            final ReportType reportType = Arrays.stream(ReportType.values()).filter(reportType1 -> reportType1.getName().equalsIgnoreCase(ChatColor.stripColor(item.getItemMeta().getDisplayName()))).findFirst().orElse(null);

            if (reportType != null && potPlayer.isCanReport()) {
                RedisUtil.publishAsync(RedisUtil.onReport(this.player, this.target, reportType.getName()));

                if (CorePlugin.getInstance().getDiscordManager().getClient() != null) {
                    CorePlugin.getInstance().getDiscordManager().sendReport(player, target, reportType.getName());
                }

                potPlayer.setCanReport(false);

                new Report(reportType.getName(), this.player, this.target);

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
