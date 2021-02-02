package vip.potclub.core.menu.extend.grant;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.WoolUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GrantMainMenu extends AbstractInventoryMenu<CorePlugin> {

    private final Player player;
    private final Player target;

    public GrantMainMenu(Player player, Player target) {
        super("Grants - Select", 9*2);
        this.player = player;
        this.target = target;
        this.update();
    }

    private void update() {
        AtomicInteger i = new AtomicInteger(0);
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        Rank.getRanks().forEach(rank -> {
            if (i.get() <= 17) {
                this.inventory.setItem(i.get(), new InventoryMenuItem(Material.NAME_TAG)
                        .addLore(Arrays.asList(
                                "&b&m--------------------------------",
                                network.getSecondaryColor() + "Click to grant the " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " rank!",
                                "&b&m--------------------------------"
                        ))
                        .setDisplayName(rank.getColor() + rank.getName())
                        .create()
                );

                i.getAndIncrement();
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

            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();
            PotPlayer potPlayer = PotPlayer.getPlayer(player);

            if (item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName() != null) {
                    String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                    Rank rank = Rank.getByName(display);

                    if (rank != null) {
                        new GrantSelectDurationMenu(this.player, this.target, rank).open(player);
                    }
                }
            }
        }
    }
}
