package vip.potclub.core.menu.extend.punish;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.json.simple.parser.ParseException;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.SaltUtil;
import vip.potclub.core.util.UUIDUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map.Entry;
import java.util.UUID;

@Getter
@Setter
public class PunishSelectConfirmMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private String target;
    private String reason;
    private boolean permanent;
    private PunishmentType punishmentType;
    private long punishmentDuration;
    private boolean isSilent;

    public PunishSelectConfirmMenu(Player player, String target, String reason, PunishmentType punishmentType, long punishmentDuration, boolean permanent) {
        super("Punishment - Confirm", 9*5);
        this.player = player;
        this.target = target;
        this.reason = reason;
        this.permanent = permanent;
        this.punishmentType = punishmentType;
        this.punishmentDuration = punishmentDuration;
        this.update();
    }

    private void update() {

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        int[] intsConfirm = new int[] { 10,11,12,19,20,21,28,29,30 };
        int[] intsDecline = new int[] { 14,15,16,23,24,25,32,33,34 };

        for (int i : intsConfirm) {
            this.inventory.setItem(i, new InventoryMenuItem(Material.STAINED_CLAY, 5)
                    .setDisplayName("&cConfirm Punishment")
                    .addLore(
                            "",
                            "&7Punisher: &b" + network.getSecondaryColor() + this.player.getName(),
                            "&7Target: &b" + network.getSecondaryColor() + this.target,
                            "&7Reason: &b" + network.getSecondaryColor() + this.reason,
                            "&7Type: &b" + network.getSecondaryColor() + punishmentType.getName(),
                            "&7Duration: &b" + network.getSecondaryColor() + (permanent ? "Permanent" : DurationFormatUtils.formatDurationWords(this.getPunishmentDuration(), true, true))
                    )
                    .create());
        }

        for (int i : intsDecline) {
            this.inventory.setItem(i, new InventoryMenuItem(Material.STAINED_CLAY, 14).setDisplayName("&cCancel Punishment").addLore(Arrays.asList(
                    "",
                    "&eClick to cancel this punishment."
            )).create());
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) throws IOException, ParseException {
        if (!event.getView().getTopInventory().equals(this.inventory)) return;
        if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Confirm")) {
                Entry<UUID, String> uuidStringEntry = UUIDUtil.getUUID(this.target);

                UUID uuidKey = uuidStringEntry.getKey();
                String nameValue = uuidStringEntry.getValue();

                Punishment punishment = new Punishment(this.punishmentType, this.player.getUniqueId(), uuidKey, this.player.getName(), this.reason, new Date(System.currentTimeMillis()), this.punishmentDuration, this.permanent, new Date(), UUID.randomUUID(), SaltUtil.getRandomSaltedString(7));

                this.player.closeInventory();

                PotPlayer potPlayer = null;
                try {
                    potPlayer = PotPlayer.getPlayer(nameValue);
                } catch (Exception ignored) { }

                if (potPlayer != null) potPlayer.getPunishments().add(punishment);

                CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, this.player, nameValue, this.isSilent);
                if (potPlayer != null) potPlayer.saveWithoutRemove();

                return;
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Cancel")) {
                this.player.closeInventory();
                this.player.sendMessage(Color.translate("&cCancelled the punishment process."));
            }
        }
    }
}
