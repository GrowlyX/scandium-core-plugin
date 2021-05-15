package com.solexgames.core.menu.impl.grant;

import com.cryptomorin.xseries.XMaterial;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class GrantSelectConfirmMenu extends AbstractInventoryMenu {

    private final Player player;
    private final Document document;

    private final Rank rank;
    private final String reason;

    private final long duration;
    private final boolean permanent;

    private final String scope;

    public GrantSelectConfirmMenu(Player player, Document document, Rank rank, long duration, String reason, boolean permanent, String scope) {
        super("Confirm grant for: &b" + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name")), 9 * 5);

        this.player = player;
        this.document = document;
        this.rank = rank;
        this.duration = duration;
        this.reason = reason;
        this.permanent = permanent;
        this.scope = scope;


    }

    public void update() {
        final ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        int[] intsConfirm = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
        int[] intsDecline = new int[]{14, 15, 16, 23, 24, 25, 32, 33, 34};

        for (int i : intsConfirm) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseMaterial(), 13).setDisplayName("&a&lConfirm Grant").addLore(Arrays.asList(
                    network.getMainColor() + "&m--------------------------------",
                    network.getSecondaryColor() + "Issuer: " + network.getMainColor() + player.getDisplayName(),
                    network.getSecondaryColor() + "Target: " + network.getMainColor() + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name")),
                    network.getSecondaryColor() + "Rank: " + network.getMainColor() + rank.getColor() + rank.getItalic() + rank.getName(),
                    network.getSecondaryColor() + "Duration: " + network.getMainColor() + (isPermanent() ? "&4Forever" : DurationFormatUtils.formatDurationWords(duration, true, true)),
                    network.getSecondaryColor() + "Reason: " + network.getMainColor() + reason,
                    network.getSecondaryColor() + "Scopes: " + network.getMainColor() + scope,
                    "",
                    "&aLeft-Click to confirm this grant!",
                    network.getMainColor() + "&m--------------------------------"
            )).create());
        }

        for (int i : intsDecline) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.RED_TERRACOTTA.parseMaterial(), 14).setDisplayName("&c&lCancel Grant").addLore(Arrays.asList(
                    "",
                    "&7Click to cancel this grant!"
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

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.parseMaterial()) {
                return;
            }
            if (ChatColor.stripColor(Color.translate(event.getCurrentItem().getItemMeta().getDisplayName())).contains("Confirm")) {
                final Grant newGrant = new Grant(this.player.getUniqueId(), this.rank, System.currentTimeMillis(), this.duration, this.reason, true, this.permanent, this.scope);

                CorePlugin.getInstance().getPlayerManager().handleGrant(newGrant, this.document, this.player, CorePlugin.getInstance().getServerName(), false);
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).contains("Cancel")) {
                this.player.sendMessage(ChatColor.RED + ("You've cancelled the current granting process."));
            }

            this.player.closeInventory();
        }
    }
}
