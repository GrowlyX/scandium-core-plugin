package vip.potclub.core.menu.extend.color;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.LanguageType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.menu.extend.grant.GrantHistoryViewMenu;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.grant.Grant;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.WoolUtil;

import java.util.Arrays;

@Getter
@Setter
public class NameColorSelectMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;

    public NameColorSelectMenu(Player player) {
        super("Custom Color", 9*2);
        this.player = player;
        this.update();
    }

    private void update() {
        for (int i = 0; i < 17; i++) {
            for (ChatColor chatColor : ChatColor.values()) {
                if (chatColor.isColor()) {
                    this.inventory.setItem(i, new InventoryMenuItem(Material.WOOL, WoolUtil.getByColor(chatColor))
                            .setDisplayName(chatColor + chatColor.toString().replace("_", " "))
                            .addLore(Arrays.asList(
                                    "&b&m------------------------------------",
                                    "&eClick to select the " + chatColor + chatColor.toString() + " &ecolor!",
                                    "&b&m------------------------------------"
                            ))
                            .create()
                    );
                }
            }
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

            if (item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName() != null) {
                    String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                    ChatColor chatColor = ChatColor.valueOf(display.replace(" ", "_"));

                    if (chatColor != null) {
                        if (potPlayer.isHasVoted()) {
                            potPlayer.setCustomColor(chatColor);
                            player.sendMessage(Color.translate("&aChanged your chat color to " + chatColor + chatColor.toString() + "&a!"));
                        } else {
                            player.sendMessage(Color.translate("&cYou cannot change chat colors!"));
                            player.sendMessage(Color.translate("&cTo be able to chat chat colors, vote for us on NameMC!"));
                            player.sendMessage(Color.translate("&chttps://namemc.com/" + CorePlugin.getInstance().getServerManager().getNetwork().getWebsiteLink() + "/"));
                        }
                    }
                }
            }
        }
    }
}
