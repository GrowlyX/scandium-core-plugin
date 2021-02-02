package vip.potclub.core.menu.extend.grant;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.punishment.PunishmentDuration;
import vip.potclub.core.player.ranks.Rank;

import java.util.Arrays;

@Getter
@Setter
public class GrantSelectDurationMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;
    private Rank rank;

    public GrantSelectDurationMenu(Player player, Player target, Rank rank) {
        super("Grants - Duration", 9*3);
        this.player = player;
        this.target = target;
        this.rank = rank;

        this.update();
    }

    private void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(10, new InventoryMenuItem(Material.INK_SACK, 1).setDisplayName(network.getMainColor() + "1 Day").addLore(Arrays.asList("", "&7Click to select this duration.")).create());
        this.inventory.setItem(11, new InventoryMenuItem(Material.INK_SACK, 2).setDisplayName(network.getMainColor() + "1 Week").addLore(Arrays.asList("", "&7Click to select this duration.")).create());
        this.inventory.setItem(12, new InventoryMenuItem(Material.INK_SACK, 3).setDisplayName(network.getMainColor() + "1 Month").addLore(Arrays.asList("", "&7Click to select this duration.")).create());
        this.inventory.setItem(13, new InventoryMenuItem(Material.INK_SACK, 4).setDisplayName(network.getMainColor() + "3 Months").addLore(Arrays.asList("", "&7Click to select this duration.")).create());
        this.inventory.setItem(14, new InventoryMenuItem(Material.INK_SACK, 5).setDisplayName(network.getMainColor() + "6 Months").addLore(Arrays.asList("", "&7Click to select this duration.")).create());
        this.inventory.setItem(15, new InventoryMenuItem(Material.INK_SACK, 6).setDisplayName(network.getMainColor() + "1 Year").addLore(Arrays.asList("", "&7Click to select this duration.")).create());
        this.inventory.setItem(16, new InventoryMenuItem(Material.INK_SACK, 14).setDisplayName(network.getMainColor() + "&4Permanent").addLore(Arrays.asList("", "&7Click to select this duration.")).create());
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
                    new GrantSelectReasonMenu(this.player, this.target, PunishmentDuration.DAY.getDuration(), this.rank, false).open(player);
                    break;
                case 11:
                    new GrantSelectReasonMenu(this.player, this.target, PunishmentDuration.WEEK.getDuration(), this.rank, false).open(player);
                    break;
                case 12:
                    new GrantSelectReasonMenu(this.player, this.target, PunishmentDuration.MONTH.getDuration(), this.rank, false).open(player);
                    break;
                case 13:
                    new GrantSelectReasonMenu(this.player, this.target, PunishmentDuration.MONTH.getDuration() * 3L, this.rank, false).open(player);
                    break;
                case 14:
                    new GrantSelectReasonMenu(this.player, this.target, PunishmentDuration.MONTH.getDuration() * 6L, this.rank, false).open(player);
                    break;
                case 15:
                    new GrantSelectReasonMenu(this.player, this.target, PunishmentDuration.YEAR.getDuration(), this.rank, false).open(player);
                    break;
                case 16:
                    new GrantSelectReasonMenu(this.player, this.target, 2147483647L, this.rank, true).open(player);
                    break;
            }
        }
    }
}
