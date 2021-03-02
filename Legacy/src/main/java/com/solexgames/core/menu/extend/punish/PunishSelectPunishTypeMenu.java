package com.solexgames.core.menu.extend.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

@Getter
@Setter
public class PunishSelectPunishTypeMenu extends AbstractInventoryMenu {

    private Player player;
    private String target;

    public PunishSelectPunishTypeMenu(Player player, String target) {
        super("Punishment type for: &b" + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target), 9*3);
        this.player = player;
        this.target = target;
        this.update();
    }

    public void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Ban").addLore(Collections.singletonList("&7Click to select this punishment.")).create());
        this.inventory.setItem(11, new ItemBuilder(Material.INK_SACK, 2).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Kick").addLore(Collections.singletonList("&7Click to select this punishment.")).create());
        this.inventory.setItem(12, new ItemBuilder(Material.INK_SACK, 3).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Mute").addLore(Collections.singletonList("&7Click to select this punishment.")).create());
        this.inventory.setItem(13, new ItemBuilder(Material.INK_SACK, 4).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Warn").addLore(Collections.singletonList("&7Click to select this punishment.")).create());
        this.inventory.setItem(14, new ItemBuilder(Material.INK_SACK, 5).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Blacklist").addLore(Collections.singletonList("&7Click to select this punishment.")).create());
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

            if (item == null || item.getType() == Material.AIR) return;
            switch (event.getRawSlot()) {
                case 10:
                    if (this.player.hasPermission("scandium.punishments.ban")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.BAN).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
                    }
                    break;
                case 11:
                    if (this.player.hasPermission("scandium.punishments.kick")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.KICK).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
                    }
                    break;
                case 12:
                    if (this.player.hasPermission("scandium.punishments.mute")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.MUTE).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
                    }
                    break;
                case 13:
                    if (this.player.hasPermission("scandium.punishments.warn")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.WARN).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
                    }
                    break;
                case 14:
                    if (this.player.hasPermission("scandium.punishments.blacklist")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.BLACKLIST).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
                    }
                    break;
            }
        }
    }
}
