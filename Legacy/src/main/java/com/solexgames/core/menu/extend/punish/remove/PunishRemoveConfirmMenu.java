package com.solexgames.core.menu.extend.punish.remove;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.menu.extend.grant.history.GrantHistoryViewMenu;
import com.solexgames.core.menu.extend.punish.history.PunishHistoryViewSubMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
public class PunishRemoveConfirmMenu extends AbstractInventoryMenu {

    public Player player;
    public String target;
    public Punishment punishment;

    public PunishRemoveConfirmMenu(Player player, String target, Punishment punishment) {
        super("Punishment removal for: " + target, 9 * 5);
        this.punishment = punishment;
        this.player = player;
        this.target = target;
        this.update();
    }

    @Override
    public void update() {
        int[] intsConfirm = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
        int[] intsDecline = new int[]{14, 15, 16, 23, 24, 25, 32, 33, 34};

        for (int i : intsConfirm) {
            this.inventory.setItem(i, new ItemBuilder(Material.STAINED_CLAY, 5)
                    .setDisplayName("&aConfirm Remove")
                    .addLore(
                            "&7Would you like to remove:",
                            "&e#" + punishment.getPunishIdentification() + "&7 from &b" + target + "&7?",
                            "",
                            "&aClick to confirm punish removal."
                    )
                    .create()
            );
        }

        for (int i : intsDecline) {
            this.inventory.setItem(i, new ItemBuilder(Material.STAINED_CLAY, 14).setDisplayName("&cCancel Remove").addLore(Arrays.asList(
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

            if (item == null || item.getType() == Material.AIR) return;
            if (ChatColor.stripColor(Color.translate(item.getItemMeta().getDisplayName())).contains("Confirm")) {
                Punishment.getAllPunishments().remove(punishment);
                RedisUtil.writeAsync(RedisUtil.fRemovePunishment(punishment));

                new PunishHistoryViewSubMenu(target, punishment.getPunishmentType()).open(player);
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).contains("Cancel")) {
                player.sendMessage(Color.translate("&cYou've cancelled the current punishment remove process."));
                player.closeInventory();
            }
        }
    }
}
