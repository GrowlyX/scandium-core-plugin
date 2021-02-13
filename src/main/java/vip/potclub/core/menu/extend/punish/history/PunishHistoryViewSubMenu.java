package vip.potclub.core.menu.extend.punish.history;

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
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.menu.extend.grant.GrantHistoryViewMenu;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.grant.Grant;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Setter
public class PunishHistoryViewSubMenu extends AbstractInventoryMenu<CorePlugin> {

    private String target;
    private PunishmentType punishmentType;

    public PunishHistoryViewSubMenu(String target, PunishmentType punishmentType) {
        super("Punishment - " + punishmentType.getName(), 9*5);
        this.target = target;
        this.punishmentType = punishmentType;
        this.update();
    }

    private void update() {
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

                    lore.add("&b&m------------------------------------");
                    lore.add("&ePunish By: &b" + network.getMainColor() + (issuerOfflinePlayer != null ? issuerOfflinePlayer.getName() : "&4Console"));
                    lore.add("&ePunish To: &b" + network.getMainColor() + targetOfflinePlayer.getName());
                    lore.add("&ePunish Reason: &b" + network.getMainColor() + punishment.getReason());
                    lore.add("&b&m------------------------------------");
                    lore.add("&ePunish Type: &b" + network.getMainColor() + punishment.getPunishmentType().getName());
                    lore.add("&ePunish Status: &b" + network.getMainColor() + statusLore);
                    lore.add("&ePunish Expiring: &b" + network.getMainColor() + punishment.getExpirationString());
                    lore.add("&b&m------------------------------------");

                    if (punishment.isRemoved()) {
                        lore.add("&eRemoved By: &b" + network.getMainColor() + (punishment.getRemoverName().equals("Console") ? "&4Console" : Bukkit.getOfflinePlayer(punishment.getRemover()).getName()));
                        lore.add("&eRemoved Reason: &b" + network.getMainColor() + punishment.getRemovalReason());
                        lore.add("&b&m------------------------------------");
                    }

                    this.inventory.setItem(i.get(), new InventoryMenuItem(Material.WOOL, (punishment.isActive() ? 5 : 14))
                            .setDisplayName(ChatColor.RED + "#" + punishment.getPunishIdentification())
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
                    String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                    String id = display.replace("#", "");
                    Punishment punishment = Punishment.getByIdentification(id);

                    if (punishment != null) {
                        Punishment.getAllPunishments().remove(punishment);
                        RedisUtil.writeAsync(RedisUtil.fRemovePunishment(punishment));

                        player.sendMessage(Color.translate("&aRemoved that punishment from &b" + this.target + "'s &ahistory!"));

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
