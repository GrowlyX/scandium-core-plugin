package com.solexgames.core.menu.impl.grant.remove;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.impl.GrantViewPaginatedMenu;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

@Getter
public class GrantRemoveConfirmMenu extends AbstractInventoryMenu {

    public Player player;
    public Player target;
    public Grant grant;

    public GrantRemoveConfirmMenu(Player player, Player target, Grant grant) {
        super("Grant removal for: " + target.getDisplayName(), 9*5);
        this.grant = grant;
        this.player = player;
        this.target = target;
        this.update();
    }

    @Override
    public void update() {
        int[] intsConfirm = new int[] { 10,11,12,19,20,21,28,29,30 };
        int[] intsDecline = new int[] { 14,15,16,23,24,25,32,33,34 };

        for (int i : intsConfirm) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.LIME_TERRACOTTA.parseMaterial(), 13)
                    .setDisplayName("&aConfirm Remove")
                    .addLore(
                            "&7Would you like to remove:",
                            "&b#" + grant.getId() + "&7 from " + target.getDisplayName() + "&7?",
                            "",
                            "&aClick to confirm grant removal."
                    )
                    .create()
            );
        }

        for (int i : intsDecline) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.RED_TERRACOTTA.parseMaterial(), 14).setDisplayName("&cCancel Remove").addLore(Arrays.asList(
                    "",
                    "&aClick to cancel this grant!"
            )).create());
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
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.target);

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            if (ChatColor.stripColor(Color.translate(item.getItemMeta().getDisplayName())).contains("Confirm")) {
                potPlayer.getAllGrants().remove(grant);
                potPlayer.setupPlayer();

                player.sendMessage(ChatColor.GREEN + Color.translate("Removed the grant from " + potPlayer.getPlayer().getDisplayName() + ChatColor.GREEN + "'s history!"));

                new GrantViewPaginatedMenu(this.player, this.target).openMenu(this.player);
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).contains("Cancel")) {
                player.sendMessage(ChatColor.RED + ("You've cancelled the current grant remove process."));
                player.closeInventory();
            }
        }
    }
}
