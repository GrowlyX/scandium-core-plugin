package vip.potclub.core.menu.extend.punish;

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
import vip.potclub.core.player.punishment.PunishmentType;

@Getter
@Setter
public class PunishSelectDurationMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private String target;
    private String reason;
    private PunishmentType punishmentType;

    public PunishSelectDurationMenu(Player player, String target, String reason, PunishmentType punishmentType) {
        super("Punishment - Duration", 9*3);
        this.player = player;
        this.target = target;
        this.reason = reason;
        this.punishmentType = punishmentType;
        this.update();
    }

    private void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        this.inventory.setItem(10, new InventoryMenuItem(Material.INK_SACK, 1).setDisplayName(network.getSecondaryColor() + "1 Day").create());
        this.inventory.setItem(11, new InventoryMenuItem(Material.INK_SACK, 2).setDisplayName(network.getSecondaryColor() + "1 Week").create());
        this.inventory.setItem(12, new InventoryMenuItem(Material.INK_SACK, 3).setDisplayName(network.getSecondaryColor() + "1 Month").create());
        this.inventory.setItem(13, new InventoryMenuItem(Material.INK_SACK, 4).setDisplayName(network.getSecondaryColor() + "3 Months").create());
        this.inventory.setItem(14, new InventoryMenuItem(Material.INK_SACK, 5).setDisplayName(network.getSecondaryColor() + "6 Months").create());
        this.inventory.setItem(15, new InventoryMenuItem(Material.INK_SACK, 6).setDisplayName(network.getSecondaryColor() + "1 Year").create());
        this.inventory.setItem(16, new InventoryMenuItem(Material.INK_SACK, 7).setDisplayName(network.getSecondaryColor() + "&4Permanent").create());
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
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.DAY.getDuration(), false).open(player);
                    break;
                case 11:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.WEEK.getDuration(), false).open(player);
                    break;
                case 12:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.MONTH.getDuration(), false).open(player);
                    break;
                case 13:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.MONTH.getDuration() * 3L, false).open(player);
                    break;
                case 14:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.MONTH.getDuration() * 6L, false).open(player);
                    break;
                case 15:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.YEAR.getDuration(), false).open(player);
                    break;
                case 16:
                    new PunishSelectConfirmMenu(this.player, this.target, this.reason, this.punishmentType, PunishmentDuration.PERMANENT.getDuration(), true).open(player);
                    break;
            }
        }
    }
}
