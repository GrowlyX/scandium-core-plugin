package vip.potclub.core.menu.extend;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.LanguageType;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

@Getter
@Setter
public class ScandiumMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;

    public ScandiumMenu(Player player) {
        super("Scandium - Control Panel", 9);
        this.player = player;
        this.update();
    }

    private void update() {
        this.inventory.setItem(2, new InventoryMenuItem(Material.INK_SACK, 11)
                .setDisplayName("&eReload Files")
                .addLore(
                        "",
                        "&7Would you like to reload",
                        "&7configurations?",
                        "",
                        "&eClick to reload files."
                )
                .create()
        );

        this.inventory.setItem(4, new InventoryMenuItem(Material.INK_SACK, 6)
                .setDisplayName("&bScandium Core")
                .addLore(
                        "",
                        "&7Thanks for purchasing",
                        "&7Scandium core!",
                        "  ",
                        "&7Support: &bGrowlyX#1337",
                        "&7Pricing: &b$45",
                        "",
                        "&eClick to contact GrowlyX."
                )
                .create()
        );

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        this.inventory.setItem(6, new InventoryMenuItem(Material.INK_SACK, 14)
                .setDisplayName("&6Network Info &7(" + network.getServerName() + ")")
                .addLore(
                        "",
                        "&7Server Name: &f" + network.getServerName(),
                        "&7Server ID: &f" + network.getServerId(),
                        "&7Primary Color: &f" + network.getMainColor() + "Color One",
                        "&7Secondary Color: &f" + network.getSecondaryColor() + "Color Two",
                        "&7General Prefix: &f" + network.getGeneralPrefix(),
                        "&7Discord Link: &f" + network.getDiscordLink(),
                        "&7Store Link: &f" + network.getStoreLink(),
                        "&7Twitter Link: &f" + network.getTwitterLink(),
                        "&7Website Link: &f" + network.getWebsiteLink(),
                        "",
                        "&7Main Developer: &f" + network.getMainDeveloper(),
                        "&7Main Owner: &f" + network.getMainOwner(),
                        ""
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
            if (event.getRawSlot() == 2) {
                CorePlugin.getInstance().reloadConfig();
                player.sendMessage(Color.translate("&aReloaded the main config!"));
            }
            if (event.getRawSlot() == 4) {
                player.sendMessage(Color.translate("&aContact: &6https://dsc.bio/GrowlyX/."));
            }
        }
    }
}
