package vip.potclub.core.menu.extend.punish;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.AbstractMenuItem;
import vip.potclub.core.player.punishment.PunishmentDuration;
import vip.potclub.core.player.punishment.PunishmentType;

@Getter
@Setter
public class PunishSelectPunishTypeMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;

    public PunishSelectPunishTypeMenu(Player player, Player target) {
        super("Punishment - Type", 9*3);
        this.player = player;
        this.target = target;
        this.update();
    }

    private void update() {
        while (this.inventory.firstEmpty() != -1) {
            this.inventory.setItem(this.inventory.firstEmpty(), new AbstractMenuItem(Material.STAINED_GLASS_PANE, 7).setDisplayname(" ").create());
        }

        this.inventory.setItem(10, new AbstractMenuItem(Material.BARRIER).setDisplayname("&aBan").create());
        this.inventory.setItem(11, new AbstractMenuItem(Material.WOOD_AXE).setDisplayname("&eKick").create());
        this.inventory.setItem(12, new AbstractMenuItem(Material.SLIME_BALL).setDisplayname("&6Mute").create());
        this.inventory.setItem(13, new AbstractMenuItem(Material.PAPER).setDisplayname("&3Warn").create());
        this.inventory.setItem(14, new AbstractMenuItem(Material.BARRIER).setDisplayname("&4Blacklist").create());
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
                    new PunishSelectReasonMenu(this.player, this.target, PunishmentType.BAN).open(player);
                    break;
                case 11:
                    new PunishSelectReasonMenu(this.player, this.target, PunishmentType.KICK).open(player);
                    break;
                case 12:
                    new PunishSelectReasonMenu(this.player, this.target, PunishmentType.MUTE).open(player);
                    break;
                case 13:
                    new PunishSelectReasonMenu(this.player, this.target, PunishmentType.WARN).open(player);
                    break;
                case 14:
                    new PunishSelectReasonMenu(this.player, this.target, PunishmentType.BLACKLIST).open(player);
                    break;
            }
        }
    }
}
