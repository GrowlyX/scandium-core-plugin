package com.solexgames.core.menu.extend.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.menu.extend.grant.remove.GrantRemoveConfirmMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GrantHistoryViewMenu extends AbstractInventoryMenu {

    private final Player player;
    private final Player target;

    public GrantHistoryViewMenu(Player player, Player target) {
        super("Applicable grants of " + target.getDisplayName(), 9 * 5);
        this.player = player;
        this.target = target;
        this.update();
    }

    public void update() {

        AtomicInteger i = new AtomicInteger(10);
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

        potPlayer.getAllGrants().forEach(grant -> {
            if (i.get() <= 34) {
                List<String> lore = new ArrayList<>();
                lore.add(network.getMainColor() + "&m------------------------------------");
                lore.add("&eTarget&7: " + network.getMainColor() + target.getDisplayName());
                lore.add("&eRank&7: " + network.getMainColor() + grant.getRank().getColor() + grant.getRank().getName());
                lore.add("&eDuration&7: " + network.getMainColor() + (grant.isPermanent() ? "&4Forever" : DurationFormatUtils.formatDurationWords(grant.getDuration(), true, true)));
                lore.add(network.getMainColor() + "&m------------------------------------");
                lore.add(network.getSecondaryColor() + "Scopes:");
                lore.add(" &7- " + network.getMainColor() + (grant.getScope() != null ? grant.getScope() : "global"));
                lore.add(network.getMainColor() + "&m------------------------------------");
                lore.add("&eIssued By&7: " + network.getMainColor() + (grant.getIssuer() != null ? Bukkit.getOfflinePlayer(grant.getIssuer()).getName() : "&4Console"));
                lore.add("&eIssued On&7: " + network.getMainColor() + CorePlugin.FORMAT.format(new Date(grant.getDateAdded())));
                lore.add("&eIssued At&7: " + network.getMainColor() + (grant.getIssuedServer() != null ? grant.getIssuedServer() : "Not Recorded"));
                lore.add("&eIssued Reason&7: " + network.getMainColor() + grant.getReason());
                lore.add(network.getMainColor() + "&m------------------------------------");
                lore.add("&aRight-Click to remove this grant from history.");
                lore.add(network.getMainColor() + "&m------------------------------------");

                this.inventory.setItem(i.get(), new ItemBuilder(Material.WOOL, (grant.isActive() ? 5 : (grant.isExpired() ? 8 : 14)))
                        .setDisplayName(ChatColor.RED + "#" + grant.getId())
                        .addLore(Color.translate(lore))
                        .create()
                );

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

            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.target);

            if (item == null || item.getType() == Material.AIR) return;
            if (item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName() != null) {
                    if (event.getClick().equals(ClickType.RIGHT)) {
                        String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        String id = display.replace("#", "");
                        Grant grant = potPlayer.getById(id);

                        if (grant != null) {
                            new GrantRemoveConfirmMenu(this.player, target, grant).open(player);
                        }
                    }
                }
            }
        }
    }
}
