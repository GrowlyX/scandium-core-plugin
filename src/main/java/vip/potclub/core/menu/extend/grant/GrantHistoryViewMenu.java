package vip.potclub.core.menu.extend.grant;

import lombok.Getter;
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
        while (this.inventory.firstEmpty() != -1) {
            this.inventory.setItem(this.inventory.firstEmpty(), new InventoryMenuItem(Material.STAINED_GLASS_PANE, 7).setDisplayName(" ").create());
        }

        AtomicInteger i = new AtomicInteger(10);
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        PotPlayer potPlayer = PotPlayer.getPlayer(target);

        potPlayer.getAllGrants().forEach(grant -> {
            if (i.get() <= 34) {
                List<String> lore = new ArrayList<>();
                lore.add("&b&m------------------------------------");
                lore.add("&7Rank: " + network.getSecondaryColor() + grant.getRank().getColor() + grant.getRank().getName());
                lore.add("&7Reason: " + network.getSecondaryColor()  + grant.getReason());
                lore.add("&7Issuer: " + network.getSecondaryColor()  + Bukkit.getOfflinePlayer(grant.getIssuer()).getName());
                lore.add("&7Addition: " + network.getSecondaryColor()  + CorePlugin.FORMAT.format(new Date(grant.getDateAdded())));
                lore.add("&7Expiration: " + network.getSecondaryColor()  + CorePlugin.FORMAT.format(new Date(grant.getDateAdded() + grant.getDuration())));
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
