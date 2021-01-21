package vip.potclub.core.menu.extend.punish;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.AbstractMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentDuration;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Getter
@Setter
public class PunishSelectConfirmMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;
    private String reason;
    private boolean permanent;
    private PunishmentType punishmentType;
    private long punishmentDuration;
    private boolean isSilent;

    public PunishSelectConfirmMenu(Player player, Player target, String reason, PunishmentType punishmentType, long punishmentDuration, boolean permanent) {
        super("Punishment - Confirm", 9*3);
        this.player = player;
        this.target = target;
        this.reason = reason;
        this.permanent = permanent;
        this.punishmentType = punishmentType;
        this.punishmentDuration = punishmentDuration;
        this.update();
    }

    private void update() {
        while (this.inventory.firstEmpty() != -1) {
            this.inventory.setItem(this.inventory.firstEmpty(), new AbstractMenuItem(Material.STAINED_GLASS_PANE, 7).setDisplayname(" ").create());
        }

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(12, new AbstractMenuItem(Material.INK_SACK, 14)
                .setDisplayname(network.getMainColor() + ChatColor.BOLD.toString() + "Silent?")
                .addLore(
                        "",
                        "&7Current: " + network.getSecondaryColor() + (isSilent ? "Enabled" : "Disabled")
                )
                .create());

        this.inventory.setItem(14, new AbstractMenuItem(Material.INK_SACK, 14)
                .setDisplayname(network.getMainColor() + ChatColor.BOLD.toString() + "Confirm")
                .addLore(
                        "",
                        "&7Punisher: &b" + network.getSecondaryColor() + this.player.getName(),
                        "&7Target: &b" + network.getSecondaryColor() + this.target.getName(),
                        "&7Reason: &b" + network.getSecondaryColor() + this.reason,
                        "&7Type: &b" + network.getSecondaryColor() + punishmentType.getName(),
                        "&7Duration: &b" + network.getSecondaryColor() + (permanent ? "Permanent" : DurationFormatUtils.formatDurationWords(this.getPunishmentDuration(), true, true))
                )
                .create());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTopInventory().equals(this.inventory)) return;
        if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            if (event.getRawSlot() == 14) {
                Punishment punishment = new Punishment(this.punishmentType, this.player.getUniqueId(), this.target.getUniqueId(), this.player.getName(), this.reason, new Date(System.currentTimeMillis()), this.punishmentDuration, this.permanent);
                punishment.savePunishment();
                this.player.sendMessage(Color.translate("&aPunished the player '" + this.target.getName() + "' with the ID '" + punishment.getId() + "'."));
                this.player.closeInventory();
                PotPlayer potPlayer = PotPlayer.getPlayer(this.target);
                potPlayer.getPunishments().add(punishment);
                CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, this.player, this.target, this.isSilent);
            }
            if (event.getRawSlot() == 12) {
                this.isSilent = !this.isSilent;
                this.update();
            }
        }
    }
}
