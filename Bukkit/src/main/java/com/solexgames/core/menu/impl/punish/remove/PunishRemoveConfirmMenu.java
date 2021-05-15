package com.solexgames.core.menu.impl.punish.remove;

import com.cryptomorin.xseries.XMaterial;
import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.impl.PunishViewPaginatedMenu;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class PunishRemoveConfirmMenu extends AbstractInventoryMenu {

    public Player player;
    public String target;
    public Punishment punishment;
    public UUID uuid;

    public PunishRemoveConfirmMenu(Player player, String target, UUID uuid, Punishment punishment) {
        super("Punishment removal for: &e" + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target), 9 * 5);

        this.punishment = punishment;
        this.player = player;
        this.target = target;
        this.uuid = uuid;


    }

    @Override
    public void update() {
        while (inventory.firstEmpty() != -1) {
            inventory.setItem(inventory.firstEmpty(), new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial())
                    .setDurability(7)
                    .setDisplayName(" ")
                    .create());
        }

        int[] intsConfirm = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
        int[] intsDecline = new int[]{14, 15, 16, 23, 24, 25, 32, 33, 34};

        for (int i : intsConfirm) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.LIME_TERRACOTTA.parseMaterial(), 13)
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

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            if (ChatColor.stripColor(Color.translate(item.getItemMeta().getDisplayName())).contains("Confirm")) {
                CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().deleteOne(Filters.eq("id", this.punishment.getId().toString())));

                Punishment.getAllPunishments().remove(this.punishment);
                RedisUtil.publishAsync(RedisUtil.fRemovePunishment(this.punishment));

                player.sendMessage(Color.SECONDARY_COLOR + "You've removed the grant with the ID: " + Color.MAIN_COLOR + "#" + punishment.getPunishIdentification() + Color.SECONDARY_COLOR + " from " + Color.MAIN_COLOR + target + Color.SECONDARY_COLOR + "'s history!");

                new PunishViewPaginatedMenu(player, this.target, this.uuid, this.punishment.getPunishmentType()).openMenu(player);
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).contains("Cancel")) {
                player.sendMessage(ChatColor.RED + ("You've cancelled the current punishment remove process."));
                player.closeInventory();
            }
        }
    }
}
