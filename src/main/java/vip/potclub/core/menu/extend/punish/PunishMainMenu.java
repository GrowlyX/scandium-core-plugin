package vip.potclub.core.menu.extend.punish;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.AbstractMenuItem;
import vip.potclub.core.util.Color;

import java.util.Arrays;

@Getter
@Setter
public class PunishMainMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;

    public PunishMainMenu(Player player, Player target) {
        super("Punishment - Main", 9*3);
        this.player = player;
        this.target = target;
        this.update();
    }

    private void update() {
        while (this.inventory.firstEmpty() != -1) {
            this.inventory.setItem(this.inventory.firstEmpty(), new AbstractMenuItem(Material.STAINED_GLASS_PANE, 7).setDisplayname(" ").create());
        }

        ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
        playerheadmeta.setOwner(target.getName());
        playerheadmeta.setLore(Arrays.asList(
                Color.translate("&7  "),
                Color.translate("&7Welcome to the punishment"),
                Color.translate("&7menu of " + target.getName() + "!"),
                Color.translate("&7  "),
                Color.translate("&3&lCLICK THE AQUA DYE TO PUNISH!"),
                Color.translate("&6&lCLICK THE GOLD DYE TO UNPUNISH!")
        ));
        playerheadmeta.setDisplayName(Color.translate("&auSuite Punishments"));
        playerhead.setItemMeta(playerheadmeta);

        this.inventory.setItem(12, new AbstractMenuItem(Material.INK_SACK, 6)
                .setDisplayname("&3&lPunish")
                .addLore(
                        "",
                        "&7Click to punish this player."
                )
                .create()
        );

        this.inventory.setItem(13, playerhead);
        this.inventory.setItem(14, new AbstractMenuItem(Material.INK_SACK, 14)
                .setDisplayname("&6&lUnpunish")
                .addLore(
                        "",
                        "&7Click to unpunish this player."
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
            Player player = (Player) event.getWhoClicked();

            if (item == null || item.getType() == Material.AIR) return;
            switch (event.getRawSlot()) {
                case 12:
                    new PunishSelectPunishTypeMenu(player, target).open(player);
                    break;
                case 14:

                    break;
            }
        }
    }
}
