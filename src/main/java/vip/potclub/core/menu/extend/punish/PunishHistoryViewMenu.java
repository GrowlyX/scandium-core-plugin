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
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.util.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class PunishHistoryViewMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private String target;

    public PunishHistoryViewMenu(Player player, String target) {
        super("Punishment - History", 9*5);
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
            if (i.get() <= 34) {
                if (punishment.getTarget().equals(Bukkit.getOfflinePlayer(target).getUniqueId())) {
                    OfflinePlayer issuerOfflinePlayer = Bukkit.getOfflinePlayer(punishment.getIssuer());
                    OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(punishment.getTarget());
                    ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
                    List<String> lore = new ArrayList<>();

                    lore.add("&b&m------------------------------------");
                    lore.add("&7Punisher: &b" + network.getSecondaryColor() + issuerOfflinePlayer.getName());
                    lore.add("&7Target: &b" + network.getSecondaryColor() + targetOfflinePlayer.getName());
                    lore.add("&7Reason: &b" + network.getSecondaryColor() + punishment.getReason());
                    lore.add("&7Type: &b" + network.getSecondaryColor() + punishment.getPunishmentType().getName());
                    lore.add("&7Active: &b" + network.getSecondaryColor() + (punishment.isActive() ? "Yes" : "No"));
                    lore.add("&7Duration: &b" + network.getSecondaryColor() + (punishment.isPermanent() ? "Permanent" : DurationFormatUtils.formatDurationWords(punishment.getPunishmentDuration(), true, true)));
                    lore.add("&b&m------------------------------------");

                    if (punishment.isRemoved()) {
                        lore.add("&7Removed: &b" + network.getSecondaryColor() + "Yes");
                        lore.add("&7Removed By: &b" + network.getSecondaryColor() + Bukkit.getOfflinePlayer(punishment.getRemover()).getName());
                        lore.add("&7Removed For: &b" + network.getSecondaryColor() + punishment.getRemovalReason());
                        lore.add("&b&m------------------------------------");
                    }

                    this.inventory.setItem(i.get(), new InventoryMenuItem(Material.WOOL, (punishment.isActive() ? 5 : 14))
                            .setDisplayName(ChatColor.RED + "#" + punishment.getPunishIdentification())
                            .addLore(Color.translate(lore))
                            .create());

                    if ((i.get() == 16) || (i.get() == 16) || (i.get() == 25)) {
                        i.getAndIncrement();
                        i.getAndIncrement();
                        i.getAndIncrement();
                    } else {
                        i.getAndIncrement();
                    }
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
