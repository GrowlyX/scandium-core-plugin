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
import vip.potclub.util.external.ItemBuilder;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class PrefixMenu extends AbstractInventoryMenu<CorePlugin> {

    private final Player player;

    public PrefixMenu(Player player) {
        super("Prefixes", 9*5);
        this.player = player;
        this.update();
    }

    private void update() {
        AtomicInteger i = new AtomicInteger(10);
        PotPlayer potPlayer = PotPlayer.getPlayer(player);

        Prefix.getPrefixes().forEach(prefix -> {
            if (i.get() < 34) {
                ArrayList<String> lore = new ArrayList<>();
                lore.add("  ");
                lore.add("&7Prefix design:");
                lore.add(prefix.getPrefix());
                lore.add("");
                lore.add("&7Chat design:");
                lore.add(Color.translate(potPlayer.getActiveGrant().getRank().getPrefix() + player.getName() + " &7" + '»' + " " + (potPlayer.getActiveGrant().getRank().getName().contains("Default") ? "&7" : "&f") + "Hello world!"));
                lore.add("");
                if (isOwning(potPlayer, prefix)) {
                    lore.add("&eClick to apply this prefix! ");
                } else {
                    lore.add("&cYou do not own this prefix!");
                }

                this.inventory.setItem(i.get(), new InventoryMenuItem(Material.INK_SACK, (this.isOwning(potPlayer, prefix) ? 10 : 1))
                        .setDisplayName(ChatColor.GOLD + prefix.getName())
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
    }

    private boolean isOwning(PotPlayer potPlayer, Prefix prefix) {
        return potPlayer.getAllPrefixes().contains(prefix);
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
            String display = ChatColor.stripColor(player.getDisplayName());

            if (item == null || item.getType() == Material.AIR) return;
            if (Prefix.getByName(display) != null) {
                Prefix prefix = Prefix.getByName(display);
                if (this.isOwning(potPlayer, prefix)) {
                    potPlayer.setAppliedPrefix(prefix);
                    player.sendMessage(Color.translate("&aYou have updated your prefix to &6" + prefix.getName() + "&a!"));
                } else {
                    player.sendMessage(Color.translate("&cYou do not own this prefix!"));
                    player.sendMessage(Color.translate("&cYou can purchase this prefix at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + "!"));
                }
                player.closeInventory();
            }
        }
    }
}
