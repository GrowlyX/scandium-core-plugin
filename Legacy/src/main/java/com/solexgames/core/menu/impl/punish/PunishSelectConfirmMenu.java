package com.solexgames.core.menu.impl.punish;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.SaltUtil;
import com.solexgames.core.util.UUIDUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class PunishSelectConfirmMenu extends AbstractInventoryMenu {

    private Player player;
    private String target;
    private String reason;
    private boolean permanent;
    private PunishmentType punishmentType;
    private long punishmentDuration;
    private boolean isSilent;

    public PunishSelectConfirmMenu(Player player, String target, String reason, PunishmentType punishmentType, long punishmentDuration, boolean permanent) {
        super("Confirm punishment for: " + Color.translate("&b") + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target), 9*5);
        this.player = player;
        this.target = target;
        this.reason = reason;
        this.permanent = permanent;
        this.punishmentType = punishmentType;
        this.punishmentDuration = punishmentDuration;
        this.update();
    }

    public void update() {

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        int[] intsConfirm = new int[] { 10,11,12,19,20,21,28,29,30 };
        int[] intsDecline = new int[] { 14,15,16,23,24,25,32,33,34 };

        for (int i : intsConfirm) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.TERRACOTTA.parseMaterial(), 5)
                    .setDisplayName("&a&lConfirm Punishment")
                    .addLore(
                            network.getMainColor() + "&m--------------------------------",
                            network.getSecondaryColor() + "Punisher: " + network.getMainColor() + this.player.getName(),
                            network.getSecondaryColor() + "Target: " + network.getMainColor() + this.target,
                            network.getSecondaryColor() + "Reason: " + network.getMainColor() + this.reason,
                            network.getSecondaryColor() + "Type: " + network.getMainColor() + punishmentType.getName(),
                            network.getSecondaryColor() + "Duration: " + network.getMainColor() + (permanent ? "&4Forever" : DurationFormatUtils.formatDurationWords(this.getPunishmentDuration(), true, true)),
                            "",
                            "&aLeft-Click to confirm this punishment!",
                            network.getMainColor() + "&m--------------------------------"
                    )
                    .create());
        }

        for (int i : intsDecline) {
            this.inventory.setItem(i, new ItemBuilder(XMaterial.TERRACOTTA.parseMaterial(), 14).setDisplayName("&c&lCancel Punishment").addLore(Arrays.asList(
                    "",
                    "&7Click to cancel this punishment."
            )).create());
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTopInventory().equals(this.inventory)) return;
        if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.parseMaterial()) return;
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Confirm")) {
                UUID uuidKey = UUIDUtil.fetchUUID(this.target);

                if (uuidKey != null) {
                    String nameValue = UUIDUtil.fetchName(uuidKey);
                    UUID randomUuid = UUID.randomUUID();
                    String saltedString = SaltUtil.getRandomSaltedString(7);
                    Date newDate = new Date();

                    Punishment punishment = new Punishment(this.punishmentType, this.player.getUniqueId(), uuidKey, this.player.getName(), this.reason, new Date(System.currentTimeMillis()), this.punishmentDuration, this.permanent, newDate, randomUuid, saltedString, true);
                    PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(nameValue);
                    Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(uuidKey).orElse(null);

                    CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, this.player.getName(), document, this.isSilent);

                    if (potPlayer != null) {
                        potPlayer.getPunishments().add(punishment);
                        potPlayer.saveWithoutRemove();
                    }

                    RedisUtil.writeAsync(RedisUtil.executePunishment(this.punishmentType, this.player.getUniqueId(), uuidKey, this.player.getName(), this.reason, new Date(System.currentTimeMillis()), this.punishmentDuration, this.permanent, newDate, randomUuid, saltedString, this.isSilent));
                } else {
                    this.player.sendMessage(Color.translate("&cCould not find that player's UUID!"));
                }

                this.player.closeInventory();
                return;
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Cancel")) {
                this.player.closeInventory();
                this.player.sendMessage(Color.translate("&cCancelled the punishment process."));
            }
        }
    }
}
