package vip.potclub.core.menu.extend;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.AbstractMenuItem;
import vip.potclub.core.player.PotPlayer;

@Getter
@Setter
public class SettingsMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;

    public SettingsMenu(Player player) {
        super("Settings", 9);
        this.player = player;
        this.update();
    }

    private void update() {
        PotPlayer potPlayer = PotPlayer.getPlayer(this.player);
        this.inventory.setItem(0, new AbstractMenuItem(Material.PAPER)
                .setDisplayname("&3Global Chat")
                .addLore(
                        "",
                        "&7Would you like to be",
                        "&7able to view global",
                        "&7chat?",
                        " ",
                        " " + (potPlayer.isCanSeeGlobalChat() ? "&8&l" : "&7&l") + "■ &bEnabled",
                        " " + (!potPlayer.isCanSeeGlobalChat() ? "&8&l" : "&7&l") + "■ &bDisabled"
                )
                .create()
        );
        this.inventory.setItem(1, new AbstractMenuItem(Material.LEVER)
                .setDisplayname("&3Server Tips")
                .addLore(
                        "",
                        "&7Would you like to be",
                        "&7able to view server",
                        "&7tips?",
                        " ",
                        " " + (potPlayer.isCanSeeTips() ? "&8&l" : "&7&l") + "■ &bEnabled",
                        " " + (!potPlayer.isCanSeeTips() ? "&8&l" : "&7&l") + "■ &bDisabled"
                )
                .create()
        );
        this.inventory.setItem(2, new AbstractMenuItem(Material.PAINTING)
                .setDisplayname("&3Receive DMs")
                .addLore(
                        "",
                        "&7Would you like to be",
                        "&7able to receive player",
                        "&7dms?",
                        " ",
                        " " + (potPlayer.isCanReceiveDms() ? "&8&l" : "&7&l") + "■ &bEnabled",
                        " " + (!potPlayer.isCanReceiveDms() ? "&8&l" : "&7&l") + "■ &bDisabled"
                )
                .create()
        );
        this.inventory.setItem(3, new AbstractMenuItem(Material.EXP_BOTTLE)
                .setDisplayname("&3DMs Sounds")
                .addLore(
                        "",
                        "&7Would you like to be",
                        "&7able to receive dm",
                        "&7sounds?",
                        " ",
                        " " + (potPlayer.isCanReceiveDmsSounds() ? "&8&l" : "&7&l") + "■ &bEnabled",
                        " " + (!potPlayer.isCanReceiveDmsSounds() ? "&8&l" : "&7&l") + "■ &bDisabled"
                )
                .create()
        );
        /*this.inventory.setItem(3, new AbstractMenuItem(Material.EXP_BOTTLE)
                .setDisplayname("&3Event Alerts")
                .addLore(
                        "",
                        "&7Would you like to be",
                        "&7able to receive event",
                        "&7alerts?",
                        " ",
                        " " + (potPlayer.isCanReceiveDmsSounds() ? "&8&l" : "&7&l") + "■ &bEnabled",
                        " " + (!potPlayer.isCanReceiveDmsSounds() ? "&8&l" : "&7&l") + "■ &bDisabled"
                )
                .create()
        );*/
        if (player.hasPermission("scandium.staff")) {
            this.inventory.setItem(4, new AbstractMenuItem(Material.INK_SACK, 9)
                    .setDisplayname("&3Staff Messages")
                    .addLore(
                            "",
                            "&7Would you like to be",
                            "&7able to receive staff",
                            "&7messages?",
                            " ",
                            " " + (potPlayer.isCanSeeStaffMessages() ? "&8&l" : "&7&l") + "■ &bEnabled",
                            " " + (!potPlayer.isCanSeeStaffMessages() ? "&8&l" : "&7&l") + "■ &bDisabled"
                    )
                    .create()
            );
        }
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

            if (item == null || item.getType() == Material.AIR) return;
            switch (event.getRawSlot()) {
                case 0:
                    potPlayer.setCanSeeGlobalChat(!potPlayer.isCanSeeGlobalChat());
                    this.update();
                    break;
                case 1:
                    potPlayer.setCanSeeTips(!potPlayer.isCanSeeTips());
                    this.update();
                    break;
                case 2:
                    potPlayer.setCanReceiveDms(!potPlayer.isCanReceiveDms());
                    this.update();
                    break;
                case 3:
                    potPlayer.setCanReceiveDmsSounds(!potPlayer.isCanReceiveDmsSounds());
                    this.update();
                    break;
                case 4:
                    if (potPlayer.getPlayer().hasPermission("scandium.staff")) {
                        potPlayer.setCanSeeStaffMessages(!potPlayer.isCanSeeStaffMessages());
                        this.update();
                    }
                    break;
            }
        }
    }
}
