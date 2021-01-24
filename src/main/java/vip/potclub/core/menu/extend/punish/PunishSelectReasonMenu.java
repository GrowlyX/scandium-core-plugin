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
import vip.potclub.core.player.punishment.PunishmentType;

@Getter
@Setter
public class PunishSelectReasonMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;
    private PunishmentType punishmentType;

    public PunishSelectReasonMenu(Player player, Player target, PunishmentType punishmentType) {
        super("Punishment - Reason", 9*3);
        this.player = player;
        this.target = target;
        this.punishmentType = punishmentType;
        this.update();
    }

    private void update() {
        while (this.inventory.firstEmpty() != -1) {
            this.inventory.setItem(this.inventory.firstEmpty(), new InventoryMenuItem(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").create());
        }
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        this.inventory.setItem(10, new InventoryMenuItem(Material.DIAMOND_SWORD).setDisplayName(network.getSecondaryColor() + "Combat Hacks").create());
        this.inventory.setItem(11, new InventoryMenuItem(Material.PAPER).setDisplayName(network.getSecondaryColor() + "Chat Abuse").create());
        this.inventory.setItem(12, new InventoryMenuItem(Material.BED).setDisplayName(network.getSecondaryColor() + "Camping").create());
        this.inventory.setItem(13, new InventoryMenuItem(Material.BARRIER).setDisplayName(network.getSecondaryColor() + "Threats").create());
        this.inventory.setItem(14, new InventoryMenuItem(Material.PAPER).setDisplayName(network.getSecondaryColor() + "Appealed").create());
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
                    new PunishSelectDurationMenu(this.player, this.target, "Combat Hacks", this.punishmentType).open(player);
                    break;
                case 11:
                    new PunishSelectDurationMenu(this.player, this.target, "Chat Abuse", this.punishmentType).open(player);
                    break;
                case 12:
                    new PunishSelectDurationMenu(this.player, this.target, "Camping", this.punishmentType).open(player);
                    break;
                case 13:
                    new PunishSelectDurationMenu(this.player, this.target, "Threats", this.punishmentType).open(player);
                    break;
                case 14:
                    new PunishSelectDurationMenu(this.player, this.target, "Appealed", this.punishmentType).open(player);
                    break;
            }
        }
    }
}
