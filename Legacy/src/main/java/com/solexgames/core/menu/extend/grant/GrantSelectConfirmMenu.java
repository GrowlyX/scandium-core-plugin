package com.solexgames.core.menu.extend.grant;

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
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

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

        this.update();
    }

    public void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        int[] intsConfirm = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
        int[] intsDecline = new int[]{14, 15, 16, 23, 24, 25, 32, 33, 34};

        for (int i : intsConfirm) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseMaterial(), 13).setDisplayName("&a&lConfirm Grant").addLore(Arrays.asList(
                    network.getMainColor() + "&m--------------------------------",
                    network.getSecondaryColor() + "Issuer: " + network.getMainColor() + player.getDisplayName(),
                    network.getSecondaryColor() + "Target: " + network.getMainColor() + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name")),
                    network.getSecondaryColor() + "Rank: " + network.getMainColor() + rank.getColor() + rank.getName(),
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
    public void onInventoryClick(InventoryClickEvent event) throws IOException, ParseException {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.parseMaterial()) return;
            if (ChatColor.stripColor(Color.translate(event.getCurrentItem().getItemMeta().getDisplayName())).contains("Confirm")) {
                Grant newGrant;

                if (scope.equals("global")) {
                    newGrant = new Grant(player.getUniqueId(), rank, System.currentTimeMillis(), duration, reason, true, this.permanent, "global");
                } else {
                    newGrant = new Grant(player.getUniqueId(), rank, System.currentTimeMillis(), duration, reason, true, this.permanent, this.scope);
                }

                newGrant.setIssuedServer(CorePlugin.getInstance().getServerName());

                PotPlayer targetPotPlayer = null;

                try {
                    targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(document.getString("name"));
                } catch (Exception ignored) {
                }

                if (targetPotPlayer != null) {
                    targetPotPlayer.getAllGrants().add(newGrant);
                    targetPotPlayer.setupPlayer();
                    targetPotPlayer.saveWithoutRemove();

                    targetPotPlayer.getPlayer().sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                    player.sendMessage("  ");
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + targetPotPlayer.getPlayer().getDisplayName() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + this.getReason() + network.getSecondaryColor() + "."));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "Granted for scopes: " + network.getMainColor() + this.scope + network.getSecondaryColor() + "."));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "The grant will expire in " + network.getMainColor() + (newGrant.isPermanent() ? "&4Never" : DurationFormatUtils.formatDurationWords(duration, true, true) + " (" + CorePlugin.FORMAT.format(new Date(duration)) + ")")));
                } else {
                    List<Grant> allGrants = new ArrayList<>();

                    if ((!((List<String>) document.get("allGrants")).isEmpty()) || ((document.get("allGrants") != null))) {
                        List<String> allStringGrants = ((List<String>) document.get("allGrants"));
                        allStringGrants.forEach(s -> allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
                    }

                    allGrants.add(newGrant);

                    List<String> grantStrings = new ArrayList<>();
                    allGrants.forEach(grant -> grantStrings.add(grant.toJson()));

                    document.put("allGrants", grantStrings);

                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("uuid", this.document.getString("uuid")), document, new ReplaceOptions().upsert(true)));

                    player.sendMessage("  ");
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + document.getString("name") + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + this.getReason() + network.getSecondaryColor() + "."));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "Granted for scopes: " + network.getMainColor() + this.scope + network.getSecondaryColor() + "."));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "The grant will expire " + network.getMainColor() + (newGrant.isPermanent() ? "&4Never&e." : "in " + DurationFormatUtils.formatDurationWords(newGrant.getDuration(), true, true) + " (" + CorePlugin.FORMAT.format(new Date(duration)) + ")")));
                }
                player.sendMessage("  ");
                player.closeInventory();
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).contains("Cancel")) {
                player.sendMessage(Color.translate("&cYou've cancelled the current granting process."));
                player.closeInventory();
            }
        }
    }
}
