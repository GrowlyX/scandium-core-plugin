package vip.potclub.core.menu.extend.punish;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class PunishHistoryViewMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private String target;

    public PunishHistoryViewMenu(Player player, String target) {
        super("Punishment - History", 9*3);
        this.player = player;
        this.target = target;
        this.update();
    }

    private void update() {
        while (this.inventory.firstEmpty() != -1) {
            this.inventory.setItem(this.inventory.firstEmpty(), new InventoryMenuItem(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").create());
        }

        AtomicInteger i = new AtomicInteger(10);
        Punishment.getAllPunishments().forEach(punishment -> {
            if (punishment.getTarget().equals(Bukkit.getOfflinePlayer(target))) {
                if (i.get() < 16) {
                    OfflinePlayer issuerOfflinePlayer = Bukkit.getOfflinePlayer(punishment.getIssuer());
                    OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(punishment.getTarget());
                    ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

                    this.inventory.setItem(i.get(), new InventoryMenuItem(Material.INK_SACK, (punishment.isActive() ? 2 : 1))
                            .setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + punishment.getId().toString())
                            .addLore(
                                    "",
                                    "&7Punisher: &b" + network.getSecondaryColor() +  issuerOfflinePlayer.getName(),
                                    "&7Target: &b" + network.getSecondaryColor() +  targetOfflinePlayer.getName(),
                                    "&7Reason: &b" + network.getSecondaryColor() +  punishment.getReason(),
                                    "&7Type: &b" + network.getSecondaryColor() +  punishment.getPunishmentType().getName(),
                                    "&7Active: &b" + network.getSecondaryColor() +  (punishment.isActive() ? "Yes" : "No"),
                                    "&7Duration: &b" + network.getSecondaryColor() +  (punishment.isPermanent() ? "Permanent" : DurationFormatUtils.formatDurationWords(punishment.getPunishmentDuration(), true, true))
                            )
                            .create());

                    i.getAndIncrement();
                }
            }
        });
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);
        }
    }
}
