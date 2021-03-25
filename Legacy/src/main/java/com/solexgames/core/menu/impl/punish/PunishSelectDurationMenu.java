package com.solexgames.core.menu.impl.punish;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.punishment.PunishmentDuration;
import com.solexgames.core.player.punishment.PunishmentType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

@Getter
@Setter
public class PunishSelectDurationMenu extends AbstractInventoryMenu {

    private Player player;
    private String target;
    private String reason;
    private PunishmentType punishmentType;

    public PunishSelectDurationMenu(Player player, String target, String reason, PunishmentType punishmentType) {
        super("Punishment duration for: " + Color.translate("&b") + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target), 9*3);
        this.player = player;
        this.target = target;
        this.reason = reason;
        this.punishmentType = punishmentType;
        this.update();
    }

    public void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(10, new ItemBuilder(XMaterial.RED_DYE.parseMaterial(), 1).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "1 Day").addLore(Collections.singletonList("&7Click to select this duration.")).create());
        this.inventory.setItem(11, new ItemBuilder(XMaterial.GREEN_DYE.parseMaterial(), 2).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "1 Week").addLore(Collections.singletonList("&7Click to select this duration.")).create());
        this.inventory.setItem(12, new ItemBuilder(XMaterial.BROWN_DYE.parseMaterial(), 3).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "1 Month").addLore(Collections.singletonList("&7Click to select this duration.")).create());
        this.inventory.setItem(13, new ItemBuilder(XMaterial.BLUE_DYE.parseMaterial(), 4).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "3 Months").addLore(Collections.singletonList("&7Click to select this duration.")).create());
        this.inventory.setItem(14, new ItemBuilder(XMaterial.PURPLE_DYE.parseMaterial(), 5).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "6 Months").addLore(Collections.singletonList("&7Click to select this duration.")).create());
        this.inventory.setItem(15, new ItemBuilder(XMaterial.CYAN_DYE.parseMaterial(), 6).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "1 Year").addLore(Collections.singletonList("&7Click to select this duration.")).create());

        this.inventory.setItem(16, new ItemBuilder(XMaterial.LIME_TERRACOTTA.parseMaterial(), 13).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "&4&oPermanent").addLore(Collections.singletonList("&7Click to select this duration.")).create());
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

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            switch (event.getRawSlot()) {
                case 10:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.DAY.getDuration(), false).open(player);
                    break;
                case 11:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.WEEK.getDuration(), false).open(player);
                    break;
                case 12:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.MONTH.getDuration(), false).open(player);
                    break;
                case 13:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.MONTH.getDuration() * 3L, false).open(player);
                    break;
                case 14:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.MONTH.getDuration() * 6L, false).open(player);
                    break;
                case 15:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.YEAR.getDuration(), false).open(player);
                    break;
                case 16:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.PERMANENT.getDuration(), true).open(player);
                    break;
            }
        }
    }
}