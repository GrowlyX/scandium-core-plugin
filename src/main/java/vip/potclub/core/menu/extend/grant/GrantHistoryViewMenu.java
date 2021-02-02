package vip.potclub.core.menu.extend.grant;

import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GrantHistoryViewMenu extends AbstractInventoryMenu<CorePlugin> {

    private final Player player;
    private final Player target;

    public GrantHistoryViewMenu(Player player, Player target) {
        super("Grants - History", 9*5);
        this.player = player;
        this.target = target;
        this.update();
    }

    private void update() {

        AtomicInteger i = new AtomicInteger(10);
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        PotPlayer potPlayer = PotPlayer.getPlayer(target);

        potPlayer.getAllGrants().forEach(grant -> {
            if (i.get() <= 34) {
                List<String> lore = new ArrayList<>();
                lore.add("&b&m------------------------------------");
                lore.add("&eTarget&7: " + network.getMainColor() + target.getDisplayName());
                lore.add("&eRank&7: " + network.getMainColor() + grant.getRank().getColor() + grant.getRank().getName());
                lore.add("&eDuration&7: " + network.getMainColor() + (grant.getDuration() != 2147483647L ? DurationFormatUtils.formatDurationWords(grant.getDuration(), true, true) : "Forever"));
                lore.add("&b&m------------------------------------");
                lore.add("&eIssued By&7: " + network.getMainColor()  + Bukkit.getOfflinePlayer(grant.getIssuer()).getName());
                lore.add("&eIssued On&7: " + network.getMainColor()  + CorePlugin.FORMAT.format(new Date(grant.getDateAdded())));
                lore.add("&eIssued Reason&7: " + network.getMainColor()  + grant.getReason());
                lore.add("&b&m------------------------------------");

                this.inventory.setItem(i.get(), new InventoryMenuItem(Material.WOOL, (grant.isActive() ? 5 : 14))
                        .setDisplayName(ChatColor.RED + CorePlugin.FORMAT.format(new Date(grant.getDateAdded())))
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
