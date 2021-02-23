package com.solexgames.core.menu.extend.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.PunishmentDuration;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@Setter
public class GrantSelectDurationMenu extends AbstractInventoryMenu {

    private Player player;
    private Document document;
    private Rank rank;

    public GrantSelectDurationMenu(Player player, Document document, Rank rank) {
        super("Select grant duration (&62/3&8)", 9*3);
        this.player = player;
        this.document = document;
        this.rank = rank;

        this.update();
    }

    public void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDisplayName(network.getMainColor() + "1 Day").addLore(Arrays.asList("", "&eClick to select this duration.")).create());
        this.inventory.setItem(11, new ItemBuilder(Material.INK_SACK, 2).setDisplayName(network.getMainColor() + "1 Week").addLore(Arrays.asList("", "&eClick to select this duration.")).create());
        this.inventory.setItem(12, new ItemBuilder(Material.INK_SACK, 3).setDisplayName(network.getMainColor() + "1 Month").addLore(Arrays.asList("", "&eClick to select this duration.")).create());
        this.inventory.setItem(13, new ItemBuilder(Material.INK_SACK, 4).setDisplayName(network.getMainColor() + "3 Months").addLore(Arrays.asList("", "&eClick to select this duration.")).create());
        this.inventory.setItem(14, new ItemBuilder(Material.INK_SACK, 6).setDisplayName(network.getMainColor() + "1 Year").addLore(Arrays.asList("", "&eClick to select this duration.")).create());
        this.inventory.setItem(15, new ItemBuilder(Material.INK_SACK, 14).setDisplayName(network.getMainColor() + "Permanent").addLore(Arrays.asList("", "&eClick to select this duration.")).create());

        this.inventory.setItem(16, new ItemBuilder(Material.INK_SACK, 14).setDisplayName(network.getMainColor() + "Custom").addLore(Arrays.asList("", "&eClick to select a custom duration.")).create());
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
                    new GrantSelectReasonMenu(this.player, this.document, PunishmentDuration.DAY.getDuration(), this.rank, false).open(player);
                    break;
                case 11:
                    new GrantSelectReasonMenu(this.player, this.document, PunishmentDuration.WEEK.getDuration(), this.rank, false).open(player);
                    break;
                case 12:
                    new GrantSelectReasonMenu(this.player, this.document, PunishmentDuration.MONTH.getDuration(), this.rank, false).open(player);
                    break;
                case 13:
                    new GrantSelectReasonMenu(this.player, this.document, PunishmentDuration.MONTH.getDuration() * 3L, this.rank, false).open(player);
                    break;
                case 14:
                    new GrantSelectReasonMenu(this.player, this.document, PunishmentDuration.YEAR.getDuration(), this.rank, false).open(player);
                    break;
                case 15:
                    new GrantSelectReasonMenu(this.player, this.document, -1L, this.rank, true).open(player);
                    break;
                case 16:
                    PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);

                    potPlayer.setGrantDurationEditing(true);
                    potPlayer.setGrantDurationTarget(this.document);
                    potPlayer.setGrantPerm(false);
                    potPlayer.setGrantDurationRank(this.rank);

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
