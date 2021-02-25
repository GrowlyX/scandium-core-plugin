package com.solexgames.core.menu.extend.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.punishment.PunishmentDuration;
import com.solexgames.core.player.punishment.PunishmentType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class PunishSelectDurationMenu extends AbstractInventoryMenu {

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

    public void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        this.inventory.setItem(10, new ItemBuilder(Material.INK_SACK, 1).setDisplayName(network.getSecondaryColor() + "1 Day").create());
        this.inventory.setItem(11, new ItemBuilder(Material.INK_SACK, 2).setDisplayName(network.getSecondaryColor() + "1 Week").create());
        this.inventory.setItem(12, new ItemBuilder(Material.INK_SACK, 3).setDisplayName(network.getSecondaryColor() + "1 Month").create());
        this.inventory.setItem(13, new ItemBuilder(Material.INK_SACK, 4).setDisplayName(network.getSecondaryColor() + "3 Months").create());
        this.inventory.setItem(14, new ItemBuilder(Material.INK_SACK, 5).setDisplayName(network.getSecondaryColor() + "6 Months").create());
        this.inventory.setItem(15, new ItemBuilder(Material.INK_SACK, 6).setDisplayName(network.getSecondaryColor() + "1 Year").create());
        this.inventory.setItem(16, new ItemBuilder(Material.INK_SACK, 7).setDisplayName(network.getSecondaryColor() + "&4Permanent").create());
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