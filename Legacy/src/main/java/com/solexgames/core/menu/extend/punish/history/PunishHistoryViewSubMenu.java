package com.solexgames.core.menu.extend.punish.history;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Setter
public class PunishHistoryViewSubMenu extends AbstractInventoryMenu {

    private String target;
    private PunishmentType punishmentType;

    public PunishHistoryViewSubMenu(String target, PunishmentType punishmentType) {
        super("Punishment - " + punishmentType.getName(), 9*5);
        this.target = target;
        this.punishmentType = punishmentType;
        this.update();
    }

    public void update() {
        AtomicInteger i = new AtomicInteger(10);
        getSortedPunishmentsByType().forEach(punishment -> {
            if (i.get() <= 34) {
                if (punishment.getTarget().equals(Bukkit.getOfflinePlayer(target).getUniqueId())) {
                    OfflinePlayer issuerOfflinePlayer;

                    if (punishment.getIssuer() != null) {
                        issuerOfflinePlayer = Bukkit.getOfflinePlayer(punishment.getIssuer());
                    } else {
                        issuerOfflinePlayer = null;
                    }

                    OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(punishment.getTarget());
                    ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
                    List<String> lore = new ArrayList<>();
                    String statusLore = punishment.isRemoved() ? ChatColor.RED + "Removed" : (punishment.isActive() ? ChatColor.GREEN + "Active" : ChatColor.GOLD + "Expired");

                    lore.add(network.getMainColor() + "&m------------------------------------");
                    lore.add("&ePunish By: &b" + network.getMainColor() + (issuerOfflinePlayer != null ? issuerOfflinePlayer.getName() : "&4Console"));
                    lore.add("&ePunish To: &b" + network.getMainColor() + targetOfflinePlayer.getName());
                    lore.add("&ePunish Reason: &b" + network.getMainColor() + punishment.getReason());
                    lore.add(network.getMainColor() + "&m------------------------------------");
                    lore.add("&ePunish Type: &b" + network.getMainColor() + punishment.getPunishmentType().getName());
                    lore.add("&ePunish Status: &b" + network.getMainColor() + statusLore);
                    lore.add("&ePunish Expiring: &b" + network.getMainColor() + punishment.getExpirationString());
                    lore.add(network.getMainColor() + "&m------------------------------------");

                    if (punishment.isRemoved()) {
                        lore.add("&eRemoved By: &b" + network.getMainColor() + (punishment.getRemoverName().equals("Console") ? "&4Console" : Bukkit.getOfflinePlayer(punishment.getRemover()).getName()));
                        lore.add("&eRemoved Reason: &b" + network.getMainColor() + punishment.getRemovalReason());
                        lore.add(network.getMainColor() + "&m------------------------------------");
                    }

                    lore.add("&aLeft-Click to remove this punishment from history.");
                    lore.add(network.getMainColor() + "&m------------------------------------");


                    this.inventory.setItem(i.get(), new ItemBuilder(Material.WOOL, (punishment.isActive() ? 5 : 14))
                            .setDisplayName(ChatColor.RED + "#" + punishment.getPunishIdentification() + " &7(" + statusLore + "&7)")
                            .addLore(Color.translate(lore))
                            .create());

                    if ((i.get() == 16) || (i.get() == 16) || (i.get() == 25)) {
                        i.getAndIncrement();
                        i.getAndIncrement();
                        i.getAndIncrement();
                    } else i.getAndIncrement();
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

            if (item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName() != null) {
                    String display = ChatColor.stripColor(Color.translate(item.getItemMeta().getDisplayName()));
                    String id = display.replace("#", "");
                    Punishment punishment = Punishment.getByIdentification(id);

                    if (punishment != null) {
                        Punishment.getAllPunishments().remove(punishment);
                        RedisUtil.writeAsync(RedisUtil.fRemovePunishment(punishment));

                        player.sendMessage(Color.translate("&aRemoved the punishment from &b" + this.target + "'s &ahistory!"));

                        new PunishHistoryViewSubMenu(target, punishmentType).open(player);
                    }
                }
            }
        }
    }

    private List<Punishment> getSortedPunishmentsByType() {
        return Punishment.getAllPunishments().stream().filter(punishment -> punishment.getPunishmentType() == this.punishmentType).sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed()).collect(Collectors.toList());
    }
}
