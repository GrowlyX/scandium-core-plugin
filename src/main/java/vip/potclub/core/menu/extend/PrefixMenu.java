package vip.potclub.core.menu.extend;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.prefixes.Prefix;
import vip.potclub.core.util.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class PrefixMenu extends AbstractInventoryMenu<CorePlugin> {

    private final Player player;

    public PrefixMenu(Player player) {
        super("Prefixes", 9*6);
        this.player = player;
        this.update();
    }

    private void update() {
        AtomicInteger i = new AtomicInteger(10);
        PotPlayer potPlayer = PotPlayer.getPlayer(player);

        Prefix.getPrefixes().forEach(prefix -> {
            if (i.get() < 34) {
                ArrayList<String> lore = new ArrayList<>();

                boolean hasPrefix = potPlayer.getAllPrefixes().contains(prefix.getName());

                lore.add("  ");
                if (hasPrefix) {
                    lore.add("&7You own this prefix and it");
                    lore.add("&7can be enabled at any time.");
                } else {
                    lore.add("&7This prefix is currently");
                    lore.add("&7locked as you don't own it.");
                }
                lore.add("  ");
                lore.add("&7Appears in chat as");
                lore.add(prefix.getPrefix());
                lore.add("  ");
                lore.add((hasPrefix ? "&eClick to equip this prefix." : "&cYou don't own this prefix."));

                this.inventory.setItem(i.get(), new InventoryMenuItem(Material.INK_SACK, (potPlayer.getAllPrefixes().contains(prefix.getName()) ? 10 : 1))
                        .setDisplayName((hasPrefix ? "&e" : "&c") + prefix.getName())
                        .addLore(Color.translate(lore))
                        .create()
                );

                if ((i.get() == 16) || (i.get() == 25)) {
                    i.getAndIncrement();
                    i.getAndIncrement();
                    i.getAndIncrement();
                } else {
                    i.getAndIncrement();
                }
            }
        });

        this.inventory.setItem(40, new InventoryMenuItem(Material.BED).setDisplayName("&cReset Prefix").addLore(Arrays.asList("", "&7Click to reset your", "&7current applied prefix!")).create());
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

                    if (Prefix.getByName(display) != null) {
                        Prefix prefix = Prefix.getByName(display);
                        if (potPlayer.getAllPrefixes().contains(prefix.getName())) {
                            potPlayer.setAppliedPrefix(prefix);
                            player.sendMessage(Color.translate("&aYou have updated your prefix to &6" + prefix.getName() + "&a!"));
                        } else {
                            player.sendMessage(Color.translate("&cYou do not own this prefix!"));
                            player.sendMessage(Color.translate("&cYou can purchase this prefix at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + "!"));
                        }
                        player.closeInventory();
                    } else if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("Reset")){
                        potPlayer.setAppliedPrefix(null);
                        player.sendMessage(Color.translate("&aReset your prefix to default!"));
                    }
                }
            }
        }
    }
}
