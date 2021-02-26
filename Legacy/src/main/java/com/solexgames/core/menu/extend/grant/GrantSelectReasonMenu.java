package com.solexgames.core.menu.extend.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class GrantSelectReasonMenu extends AbstractInventoryMenu {

    private Player player;
    private Document document;
    private Rank rank;
    private long duration;
    private boolean permanent;
    private String scope;

    public GrantSelectReasonMenu(Player player, Document document, long duration, Rank rank, boolean permanent, String scope) {
        super("Grant reason for: " + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name")), 9*3);

        this.player = player;
        this.document = document;
        this.rank = rank;
        this.duration = duration;
        this.permanent = permanent;
        this.scope = scope;

        this.update();
    }

    public void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(10, new ItemBuilder(Material.PAPER).setDisplayName(network.getMainColor() + "Rank Migration").addLore(Arrays.asList("", "&7Click to select this reason.")).create());
        this.inventory.setItem(11, new ItemBuilder(Material.PAPER).setDisplayName(network.getMainColor() + "Promotion").addLore(Arrays.asList("", "&7Click to select this reason.")).create());
        this.inventory.setItem(12, new ItemBuilder(Material.PAPER).setDisplayName(network.getMainColor() + "Demotion").addLore(Arrays.asList("", "&7Click to select this reason.")).create());
        this.inventory.setItem(13, new ItemBuilder(Material.PAPER).setDisplayName(network.getMainColor() + "Buycraft Issues").addLore(Arrays.asList("", "&7Click to select this reason.")).create());
        this.inventory.setItem(14, new ItemBuilder(Material.PAPER).setDisplayName(network.getMainColor() + "Other").addLore(Arrays.asList("", "&7Click to select this reason.")).create());

        this.inventory.setItem(16, new ItemBuilder(Material.STAINED_CLAY, 5).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Custom").addLore(Arrays.asList("", "&7Click to choose a custom reason.")).create());
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
            switch (event.getRawSlot()) {
                case 10:
                    new GrantSelectConfirmMenu(this.player, this.document, this.rank, this.duration, "Rank Migration", this.permanent, this.scope).open(player);
                    break;
                case 11:
                    new GrantSelectConfirmMenu(this.player, this.document, this.rank, this.duration, "Promotion", this.permanent, this.scope).open(player);
                    break;
                case 12:
                    new GrantSelectConfirmMenu(this.player, this.document, this.rank, this.duration, "Demotion", this.permanent, this.scope).open(player);
                    break;
                case 13:
                    new GrantSelectConfirmMenu(this.player, this.document, this.rank, this.duration, "Buycraft Issues", this.permanent, this.scope).open(player);
                    break;
                case 14:
                    new GrantSelectConfirmMenu(this.player, this.document, this.rank, this.duration, "Other", this.permanent, this.scope).open(player);
                    break;
                case 16:
                    PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);
                    if (potPlayer != null) {
                        potPlayer.setGrantTarget(this.document);
                        potPlayer.setGrantRank(this.rank);
                        potPlayer.setGrantDuration(this.duration);
                        potPlayer.setGrantEditing(true);
                        potPlayer.setGrantScope(this.scope);

                        this.player.closeInventory();

                        this.player.sendMessage(Color.translate("  "));
                        this.player.sendMessage(Color.translate("&aType a custom reason for the grant in chat!"));
                        this.player.sendMessage(Color.translate("&7&o(Type 'cancel' to cancel this process)."));
                        this.player.sendMessage(Color.translate("  "));
                    }
                    break;
            }
        }
    }
}
