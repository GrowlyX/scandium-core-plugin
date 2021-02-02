package vip.potclub.core.menu.extend.grant;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.ranks.Rank;

import java.util.Arrays;

@Getter
@Setter
public class GrantSelectReasonMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;
    private Rank rank;
    private long duration;
    private boolean permanent;

    public GrantSelectReasonMenu(Player player, Player target, long duration, Rank rank, boolean permanent) {
        super("Grants - Reason", 9*3);
        this.player = player;
        this.target = target;
        this.rank = rank;
        this.duration = duration;
        this.permanent = permanent;

        this.update();
    }

    private void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(10, new InventoryMenuItem(Material.PAPER).setDisplayName(network.getMainColor() + "Rank Migration").addLore(Arrays.asList("", "&7Click to select this reason.")).create());
        this.inventory.setItem(11, new InventoryMenuItem(Material.PAPER).setDisplayName(network.getMainColor() + "Promotion").addLore(Arrays.asList("", "&7Click to select this reason.")).create());
        this.inventory.setItem(12, new InventoryMenuItem(Material.PAPER).setDisplayName(network.getMainColor() + "Demotion").addLore(Arrays.asList("", "&7Click to select this reason.")).create());
        this.inventory.setItem(13, new InventoryMenuItem(Material.PAPER).setDisplayName(network.getMainColor() + "Buycraft Issues").addLore(Arrays.asList("", "&7Click to select this reason.")).create());
        this.inventory.setItem(14, new InventoryMenuItem(Material.PAPER).setDisplayName(network.getMainColor() + "Other").addLore(Arrays.asList("", "&7Click to select this reason.")).create());
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
                    new GrantSelectConfirmMenu(this.player, this.target, this.rank, this.duration, "Rank Migration", permanent).open(player);
                    break;
                case 11:
                    new GrantSelectConfirmMenu(this.player, this.target, this.rank, this.duration, "Promotion", permanent).open(player);
                    break;
                case 12:
                    new GrantSelectConfirmMenu(this.player, this.target, this.rank, this.duration, "Demotion", permanent).open(player);
                    break;
                case 13:
                    new GrantSelectConfirmMenu(this.player, this.target, this.rank, this.duration, "Buycraft Issues", permanent).open(player);
                    break;
                case 14:
                    new GrantSelectConfirmMenu(this.player, this.target, this.rank, this.duration, "Other", permanent).open(player);
                    break;
            }
        }
    }
}
