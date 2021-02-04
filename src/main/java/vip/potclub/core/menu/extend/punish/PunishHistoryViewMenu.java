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
        AtomicInteger i = new AtomicInteger(10);
        Punishment.getAllPunishments().forEach(punishment -> {
            if (i.get() <= 34) {
                if (punishment.getTarget().equals(Bukkit.getOfflinePlayer(target).getUniqueId())) {
                    OfflinePlayer issuerOfflinePlayer = Bukkit.getOfflinePlayer(punishment.getIssuer());
                    OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(punishment.getTarget());
                    ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
                    List<String> lore = new ArrayList<>();

                    lore.add("&b&m------------------------------------");
                    if (issuerOfflinePlayer != null) {
                        lore.add("&ePunish By: &b" + network.getMainColor() + issuerOfflinePlayer.getName());
                    } else {
                        lore.add("&ePunish By: &b" + network.getMainColor() + "&4Console");
                    }
                    lore.add("&ePunish To: &b" + network.getMainColor() + targetOfflinePlayer.getName());
                    lore.add("&ePunish Reason: &b" + network.getMainColor() + punishment.getReason());
                    lore.add("&b&m------------------------------------");
                    lore.add("&ePunish Type: &b" + network.getMainColor() + punishment.getPunishmentType().getName());
                    lore.add("&ePunish Status: &b" + network.getMainColor() + (punishment.isActive() ? (punishment.isRemoved() ? "&4Removed" : "&aActive") : "&4Expired"));
                    lore.add("&ePunish Expiring: &b" + network.getMainColor() + punishment.getExpirationString());
                    lore.add("&b&m------------------------------------");

                    if (punishment.isRemoved()) {
                        lore.add("&eRemoved By: &b" + network.getMainColor() + Bukkit.getOfflinePlayer(punishment.getRemover()).getName());
                        lore.add("&eRemoved Reason: &b" + network.getMainColor() + punishment.getRemovalReason());
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
                    } else i.getAndIncrement();
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
