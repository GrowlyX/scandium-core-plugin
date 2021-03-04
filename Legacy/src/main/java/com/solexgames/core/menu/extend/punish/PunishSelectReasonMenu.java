package com.solexgames.core.menu.extend.punish;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
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
public class PunishSelectReasonMenu extends AbstractInventoryMenu {

    private Player player;
    private String target;
    private PunishmentType punishmentType;

    public PunishSelectReasonMenu(Player player, String target, PunishmentType punishmentType) {
        super("Punishment reason for: &b" + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target), 9*3);
        this.player = player;
        this.target = target;
        this.punishmentType = punishmentType;
        this.update();
    }

    public void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(10, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Unfair Advantage").addLore(Collections.singletonList("&7Click to select this reason.")).create());
        this.inventory.setItem(11, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Chat Abuse").addLore(Collections.singletonList("&7Click to select this reason.")).create());
        this.inventory.setItem(12, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Camping").addLore(Collections.singletonList("&7Click to select this reason.")).create());
        this.inventory.setItem(13, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Threats").addLore(Collections.singletonList("&7Click to select this reason.")).create());
        this.inventory.setItem(14, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "Appealed").addLore(Collections.singletonList("&7Click to select this reason.")).create());

        this.inventory.setItem(16, new ItemBuilder(XMaterial.LIME_TERRACOTTA.parseMaterial(), 13).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Custom").addLore(Collections.singletonList("&7Click to select a custom reason.")).create());
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
                    new PunishSelectDurationMenu(this.player, this.target, "Unfair Advantage", this.punishmentType).open(player);
                    break;
                case 11:
                    new PunishSelectDurationMenu(this.player, this.target, "Chat Abuse", this.punishmentType).open(player);
                    break;
                case 12:
                    new PunishSelectDurationMenu(this.player, this.target, "Camping", this.punishmentType).open(player);
                    break;
                case 13:
                    new PunishSelectDurationMenu(this.player, this.target, "Threats", this.punishmentType).open(player);
                    break;
                case 14:
                    new PunishSelectDurationMenu(this.player, this.target, "Appealed", this.punishmentType).open(player);
                    break;
                case 16:
                    PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);
                    if (potPlayer != null) {
                        this.player.closeInventory();

                        this.player.sendMessage(Color.translate("  "));
                        this.player.sendMessage(Color.translate("&aType a custom reason for the punishment in chat!"));
                        this.player.sendMessage(Color.translate("&7&o(Type 'cancel' to cancel this process)."));
                        this.player.sendMessage(Color.translate("  "));

                        potPlayer.setReasonEditing(true);
                        potPlayer.setReasonTarget(this.target);
                        potPlayer.setReasonType(this.punishmentType);
                    }
                    break;
            }
        }
    }
}
