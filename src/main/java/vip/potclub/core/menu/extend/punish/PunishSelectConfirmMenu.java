package vip.potclub.core.menu.extend.punish;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.AbstractMenuItem;
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

        this.inventory.setItem(13, new AbstractMenuItem(Material.INK_SACK, 14)
                .setDisplayname("&3&lConfirm")
                .addLore(
                        "",
                        "&7Punisher: &b" + this.player.getName(),
                        "&7Target: &b" + this.target.getName(),
                        "&7Reason: &b" + this.reason,
                        "&7Type: &b" + punishmentType.getName(),
                        "&7Duration: &b" + (permanent ? "Permanent" : DurationFormatUtils.formatDurationWords(this.getPunishmentDuration(), true, true))
                )
                .create());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTopInventory().equals(this.inventory)) return;
        if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            if (event.getRawSlot() == 13) {
                Punishment punishment = new Punishment(this.punishmentType, this.player.getUniqueId(), target.getUniqueId(), this.player.getName(), this.reason, new Date(System.currentTimeMillis()), this.punishmentDuration, this.permanent);
                punishment.savePunishment();
                this.player.sendMessage(Color.translate("&aPunished the player '" + target.getName() + "' with the ID '" + punishment.getId() + "'."));
            }
        }
    }
}
