package vip.potclub.core.menu.extend.grant;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.json.simple.parser.ParseException;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.grant.Grant;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.UUIDUtil;

import java.io.IOException;
import java.util.*;

@Getter
public class GrantSelectConfirmMenu extends AbstractInventoryMenu<CorePlugin> {

    private final Player player;
    private final Document document;

    private final Rank rank;
    private final String reason;

    private final long duration;
    private final boolean permanent;

    public GrantSelectConfirmMenu(Player player, Document document, Rank rank, long duration, String reason, boolean permanent) {
        super("Grants - Confirm", 9*5);

        this.player = player;
        this.document = document;
        this.rank = rank;
        this.duration = duration;
        this.reason = reason;
        this.permanent = permanent;

        this.update();
    }

    private void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        int[] intsConfirm = new int[] { 10,11,12,19,20,21,28,29,30 };
        int[] intsDecline = new int[] { 14,15,16,23,24,25,32,33,34 };

        for (int i : intsConfirm) {
            this.inventory.setItem(i, new InventoryMenuItem(Material.STAINED_CLAY, 5).setDisplayName("&aConfirm Grant").addLore(Arrays.asList(
                    network.getMainColor() + "&m--------------------------------",
                    network.getSecondaryColor() + "Issuer: " + network.getMainColor() + player.getDisplayName(),
                    network.getSecondaryColor() + "Target: " + network.getMainColor() + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name")),
                    network.getSecondaryColor() + "Rank: " + network.getMainColor() + rank.getColor() + rank.getName(),
                    network.getSecondaryColor() + "Duration: " + network.getMainColor()  + (isPermanent() ? "&4Forever" : DurationFormatUtils.formatDurationWords(duration, true, true)),
                    network.getSecondaryColor() + "Reason: " + network.getMainColor()  + reason,
                    "",
                    "&aClick to confirm this grant!",
                    network.getMainColor() + "&m--------------------------------"
            )).create());
        }

        for (int i : intsDecline) {
            this.inventory.setItem(i, new InventoryMenuItem(Material.STAINED_CLAY, 14).setDisplayName("&cCancel Grant").addLore(Arrays.asList(
                    "",
                    "&aClick to cancel this grant!"
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

            if (ChatColor.stripColor(Color.translate(event.getCurrentItem().getItemMeta().getDisplayName())).contains("Confirm")) {
                Grant newGrant = new Grant(player.getUniqueId(), rank, System.currentTimeMillis(), System.currentTimeMillis() - duration, reason, true, permanent);
                PotPlayer targetPotPlayer = PotPlayer.getPlayer(document.getString("name"));

                if (targetPotPlayer != null) {
                    targetPotPlayer.getAllGrants().add(newGrant);
                    targetPotPlayer.setupAttachment();
                    targetPotPlayer.saveWithoutRemove();

                    targetPotPlayer.getPlayer().sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + targetPotPlayer.getPlayer().getDisplayName() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + this.getReason() + network.getSecondaryColor() + "."));
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

                    Map.Entry<UUID, String> uuidStringEntry = UUIDUtil.getUUID(document.getString("name"));
                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuidStringEntry.getKey()), document, new ReplaceOptions().upsert(true)));

                    player.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + uuidStringEntry.getValue() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + this.getReason() + network.getSecondaryColor() + "."));
                }

                player.closeInventory();
            } else if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).contains("Cancel")) {
                player.sendMessage(Color.translate("&cYou've cancelled the current granting process."));
                player.closeInventory();
            }
        }
    }
}
