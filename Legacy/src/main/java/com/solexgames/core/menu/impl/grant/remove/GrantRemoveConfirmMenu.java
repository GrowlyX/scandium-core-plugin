package com.solexgames.core.menu.impl.grant.remove;

import com.cryptomorin.xseries.XMaterial;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.extend.GrantViewPaginatedMenu;
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

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

@Getter
public class GrantRemoveConfirmMenu extends AbstractInventoryMenu {

    public Player player;
    public Document target;
    public Grant grant;
    public List<Grant> allGrants;

    public GrantRemoveConfirmMenu(Player player, Document target, Grant grant, List<Grant> allGrants) {
        super("Grant removal for: " + (Rank.getByName(target.getString("rankName")) != null ? Rank.getByName(target.getString("rankName")).getColor() : ChatColor.GRAY) + target.getString("name"), 9*5);

        this.grant = grant;
        this.player = player;
        this.target = target;
        this.allGrants = allGrants;

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
                            "&b#" + grant.getId() + "&7 from " + (Rank.getByName(target.getString("rankName")) != null ? Rank.getByName(target.getString("rankName")).getColor() : ChatColor.GRAY) + target.getString("name") + "&7?",
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
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getString("name"));

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;
            if (ChatColor.stripColor(Color.translate(item.getItemMeta().getDisplayName())).contains("Confirm")) {
                if (potPlayer != null) {
                    potPlayer.getAllGrants().remove(grant);
                    potPlayer.setupPlayer();
                } else {
                    this.allGrants.remove(grant);

                    List<String> grantStrings = new ArrayList<>();
                    this.getAllGrants().forEach(grant -> grantStrings.add(grant.toJson()));

                    this.target.put("allGrants", grantStrings);

                    CorePlugin.getInstance().getMongoThread().execute(() ->
                            CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("uuid", this.target.getString("uuid")), this.target, new ReplaceOptions().upsert(true))
                    );
                }

                player.sendMessage(Color.translate("&aRemoved the grant from &b" + (Rank.getByName(target.getString("rankName")) != null ? Rank.getByName(target.getString("rankName")).getColor() : ChatColor.GRAY) + target.getString("name") + "'s &ahistory!"));
                player.closeInventory();

                new GrantViewPaginatedMenu(this.player, this.target).openMenu(this.player);
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).contains("Cancel")) {
                player.sendMessage(Color.translate("&cYou've cancelled the current grant remove process."));
                player.closeInventory();
            }
        }
    }
}
