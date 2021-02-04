package vip.potclub.core.menu.extend.punish.history;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentType;

@Getter
@Setter
public class PunishHistoryViewMainMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private String target;

    public PunishHistoryViewMainMenu(Player player, String target) {
        super("Punishment - History", 9);
        this.player = player;
        this.target = target;
        this.update();
    }

    private void update() {
        this.inventory.setItem(2, new InventoryMenuItem(Material.WOOL)
                .setDisplayName("&6Warnings")
                .addLore(
                        "",
                        "&7Click to all view warnings",
                        "&7in relation to " + this.target + ".",
                        "",
                        "&7Global warnings: " + "&b" + this.getPunishmentCountByType(PunishmentType.WARN)
                )
                .create()
        );
        this.inventory.setItem(3, new InventoryMenuItem(Material.WOOL, 4)
                .setDisplayName("&6Kicks")
                .addLore(
                        "",
                        "&7Click to all view kicks",
                        "&7in relation to " + this.target + ".",
                        "",
                        "&7Global kicks: " + "&b" + this.getPunishmentCountByType(PunishmentType.KICK)
                )
                .create()
        );
        this.inventory.setItem(4, new InventoryMenuItem(Material.WOOL, 1)
                .setDisplayName("&6Mutes")
                .addLore(
                        "",
                        "&7Click to all view mutes",
                        "&7in relation to " + this.target + ".",
                        "",
                        "&7Global mutes: " + "&b" + this.getPunishmentCountByType(PunishmentType.MUTE)
                )
                .create()
        );
        this.inventory.setItem(5, new InventoryMenuItem(Material.WOOL, 14)
                .setDisplayName("&6Bans")
                .addLore(
                        "",
                        "&7Click to all view bans",
                        "&7in relation to " + this.target + ".",
                        "",
                        "&7Global bans: " + "&b" + this.getPunishmentCountByType(PunishmentType.BAN)
                )
                .create()
        );
        this.inventory.setItem(6, new InventoryMenuItem(Material.WOOL, 15)
                .setDisplayName("&6Blacklists")
                .addLore(
                        "",
                        "&7Click to all view blacklists",
                        "&7in relation to " + this.target + ".",
                        "",
                        "&7Global blacklists: " + "&b" + this.getPunishmentCountByType(PunishmentType.BLACKLIST)
                )
                .create()
        );
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;
            switch (event.getRawSlot()) {
                case 2:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.WARN).open(this.player);
                    break;
                case 3:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.KICK).open(this.player);
                    break;
                case 4:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.MUTE).open(this.player);
                    break;
                case 5:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.BAN).open(this.player);
                    break;
                case 6:
                    new PunishHistoryViewSubMenu(this.target, PunishmentType.BLACKLIST).open(this.player);
                    break;
            }
        }
    }

    private int getPunishmentCountByType(PunishmentType punishmentType) {
        return (int) Punishment.getAllPunishments().stream()
                .filter(punishment -> punishment.getPunishmentType().equals(punishmentType))
                .count();
    }
}
