package com.solexgames.core.menu.impl.grant;

import com.cryptomorin.xseries.XMaterial;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.GrantUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.impl.grant.GrantViewPaginatedMenu;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

@Getter
public class GrantRemoveConfirmMenu extends AbstractInventoryMenu {

    public Player player;
    public Document target;
    public Grant grant;
    public String reason;
    public String fancyName;

    public List<Grant> grantList;

    public GrantRemoveConfirmMenu(Player player, Document target, Grant grant, String reason, String fancyName, List<Grant> grantList) {
        super("Grant removal for: " + fancyName, 9 * 5);

        this.grant = grant;
        this.player = player;
        this.target = target;
        this.reason = reason;
        this.fancyName = fancyName;
        this.grantList = grantList;

        this.update();
    }

    @Override
    public void update() {
        while (inventory.firstEmpty() != -1) {
            inventory.setItem(inventory.firstEmpty(), new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial())
                    .setDurability(7)
                    .setDisplayName(" ")
                    .create());
        }

        int[] intsConfirm = new int[] { 10,11,12,19,20,21,28,29,30 };
        int[] intsDecline = new int[] { 14,15,16,23,24,25,32,33,34 };

        for (int i : intsConfirm) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.LIME_TERRACOTTA.parseMaterial(), 13)
                    .setDisplayName("&aConfirm Remove")
                    .addLore(
                            "&7Would you like to remove:",
                            "&b#" + grant.getId() + "&7 from " + this.fancyName + "&7?",
                            "",
                            "&e[Click to remove grant]"
                    )
                    .create()
            );
        }

        for (int i : intsDecline) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.RED_TERRACOTTA.parseMaterial(), 14)
                    .setDisplayName(ChatColor.RED + "Cancel Process")
                    .create()
            );
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
                this.grant.setRemoved(true);
                this.grant.setRemovedBy(this.player.getDisplayName());
                this.grant.setRemovedFor(this.reason);

                this.grantList.remove(this.grant);

                final List<String> grantStrings = new ArrayList<>();
                this.grantList.forEach(grant -> grantStrings.add(grant.toJson()));

                this.target.replace("allGrants", grantStrings);
                this.target.replace("rankName", GrantUtil.getProminentGrant(grantList).getRank().getName());

                final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.target.getString("name"));

                if (potPlayer != null) {
                    potPlayer.getAllGrants().remove(potPlayer.getById(this.grant.getId()));
                    potPlayer.saveWithoutRemove();
                    potPlayer.setupPlayer();
                } else {
                    CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("uuid", this.target.getString("uuid")), this.target, new ReplaceOptions().upsert(true)));
                }

                final String fancyRank = Color.translate(this.grant.getRank().getColor() + this.grant.getRank().getName());

                player.sendMessage(Color.SECONDARY_COLOR + "You've removed a grant with the rank " + fancyRank + Color.SECONDARY_COLOR + " from " + this.fancyName + Color.SECONDARY_COLOR + ".");
                player.closeInventory();
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).contains("Cancel")) {
                player.sendMessage(ChatColor.RED + ("You've cancelled the current grant removal process."));
                player.closeInventory();
            }
        }
    }
}
