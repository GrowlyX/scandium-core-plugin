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
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;

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
            this.inventory.setItem(this.inventory.firstEmpty(), new InventoryMenuItem(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").create());
        }

        this.inventory.setItem(10, new InventoryMenuItem(Material.BARRIER).setDisplayName("&aBan").create());
        this.inventory.setItem(11, new InventoryMenuItem(Material.WOOD_AXE).setDisplayName("&eKick").create());
        this.inventory.setItem(12, new InventoryMenuItem(Material.SLIME_BALL).setDisplayName("&6Mute").create());
        this.inventory.setItem(13, new InventoryMenuItem(Material.PAPER).setDisplayName("&3Warn").create());
        this.inventory.setItem(14, new InventoryMenuItem(Material.BARRIER).setDisplayName("&4Blacklist").create());
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
                    if (this.player.hasPermission("scandium.punishments.ban")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.BAN).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cNo permission."));
                    }
                    break;
                case 11:
                    if (this.player.hasPermission("scandium.punishments.kick")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.KICK).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cNo permission."));
                    }
                    break;
                case 12:
                    if (this.player.hasPermission("scandium.punishments.mute")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.MUTE).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cNo permission."));
                    }
                    break;
                case 13:
                    if (this.player.hasPermission("scandium.punishments.warn")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.WARN).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cNo permission."));
                    }
                    break;
                case 14:
                    if (this.player.hasPermission("scandium.punishments.blacklist")) {
                        new PunishSelectReasonMenu(this.player, this.target, PunishmentType.BLACKLIST).open(player);
                    } else {
                        this.player.closeInventory();
                        player.sendMessage(Color.translate("&cNo permission."));
                    }
                    break;
            }
        }
    }
}
