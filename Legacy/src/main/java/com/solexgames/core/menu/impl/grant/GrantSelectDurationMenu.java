package com.solexgames.core.menu.impl.grant;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.DateUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

@Getter
@Setter
public class GrantSelectDurationMenu extends AbstractInventoryMenu {

    private Player player;
    private Document document;
    private Rank rank;
    private String scope;

    public GrantSelectDurationMenu(Player player, Document document, Rank rank, String scope) {
        super("Grant time for: " + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name")), 9 * 3);
        this.player = player;
        this.document = document;
        this.rank = rank;
        this.scope = scope;

        this.update();
    }

    public void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(10, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "1 Day").addLore(Collections.singletonList("&7Click to select this duration.")).create());
        this.inventory.setItem(11, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "1 Week").addLore(Collections.singletonList("&7Click to select this duration.")).create());
        this.inventory.setItem(12, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "1 Month").addLore(Collections.singletonList("&7Click to select this duration.")).create());
        this.inventory.setItem(13, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.ITALIC.toString() + "3 Months").addLore(Collections.singletonList("&7Click to select this duration.")).create());
        this.inventory.setItem(14, new ItemBuilder(XMaterial.PAPER.parseMaterial()).setDisplayName("&4&oPermanent").addLore(Collections.singletonList("&7Click to select this duration.")).create());

        this.inventory.setItem(16, new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseMaterial(), 5).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Custom").addLore(Collections.singletonList("&7Click to select a custom duration.")).create());
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
                    new GrantSelectReasonMenu(this.player, this.document, System.currentTimeMillis() - DateUtil.parseDateDiff("1d", false), this.rank, false, this.scope).open(player);
                    break;
                case 11:
                    new GrantSelectReasonMenu(this.player, this.document, System.currentTimeMillis() - DateUtil.parseDateDiff("1w", false), this.rank, false, this.scope).open(player);
                    break;
                case 12:
                    new GrantSelectReasonMenu(this.player, this.document, System.currentTimeMillis() - DateUtil.parseDateDiff("1m", false), this.rank, false, this.scope).open(player);
                    break;
                case 13:
                    new GrantSelectReasonMenu(this.player, this.document, System.currentTimeMillis() - DateUtil.parseDateDiff("3m", false), this.rank, false, this.scope).open(player);
                    break;
                case 14:
                    new GrantSelectReasonMenu(this.player, this.document, -1L, this.rank, true, this.scope).open(player);
                    break;
                case 16:
                    PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);

                    potPlayer.setGrantDurationEditing(true);
                    potPlayer.setGrantDurationTarget(this.document);
                    potPlayer.setGrantDurationPerm(false);
                    potPlayer.setGrantDurationRank(this.rank);
                    potPlayer.setGrantDurationScope(this.scope);

                    player.sendMessage(Color.translate("  "));
                    player.sendMessage(Color.translate("&aType in a custom duration in chat!"));
                    player.sendMessage(Color.translate("&7&oUse 'cancel' to cancel this process, or use 'perm' to set it as a permanent punishment!"));
                    player.sendMessage(Color.translate("  "));

                    this.player.closeInventory();
                    break;
            }
        }
    }
}
