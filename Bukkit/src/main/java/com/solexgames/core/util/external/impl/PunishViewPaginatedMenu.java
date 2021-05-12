package com.solexgames.core.util.external.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.impl.punish.remove.PunishRemoveConfirmMenu;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class PunishViewPaginatedMenu extends PaginatedMenu {

    private final Player player;
    private final String target;
    private final PunishmentType punishmentType;

    private final UUID targetUuid;

    public PunishViewPaginatedMenu(Player player, String target, PunishmentType punishmentType) {
        super(9);

        this.player = player;
        this.target = target;
        this.punishmentType = punishmentType;
        this.targetUuid = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(target);
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Applicable punishments of: " + (Bukkit.getPlayerExact(target) != null ? Bukkit.getPlayerExact(target).getDisplayName() : target);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttonMap = new HashMap<>();
        final AtomicInteger atomicInteger = new AtomicInteger();

        this.getSortedPunishmentsByType().forEach(punishment -> buttonMap.put(atomicInteger.getAndIncrement(), new PunishmentButton(punishment)));

        return buttonMap;
    }

    private List<Punishment> getSortedPunishmentsByType() {
        return Punishment.getAllPunishments().stream()
                .filter(punishment -> punishment != null && punishment.getPunishmentType().equals(this.punishmentType) && punishment.getTarget().equals(this.getTargetUuid()))
                .sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed())
                .collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    private class PunishmentButton extends Button {

        private final Punishment punishment;

        @Override
        public ItemStack getButtonItem(Player player) {
            final List<String> lore = new ArrayList<>();
            final String statusLore = this.punishment.isRemoved() ? ChatColor.RED + "[Removed]" : (this.punishment.isActive() ? ChatColor.GREEN + "[Active]" : ChatColor.GOLD + "[Expired]");

            lore.add(Color.MAIN_COLOR + ChatColor.STRIKETHROUGH.toString() + "------------------------------------");
            lore.add(Color.SECONDARY_COLOR + "Punish By: " + Color.MAIN_COLOR + (this.punishment.getIssuer() == null ? ChatColor.DARK_RED + "Console" : this.punishment.getIssuerName()));
            lore.add(Color.SECONDARY_COLOR + "Punish To: " + Color.MAIN_COLOR + PunishViewPaginatedMenu.this.target);
            lore.add(Color.SECONDARY_COLOR + "Punish On: " + Color.MAIN_COLOR + CorePlugin.FORMAT.format(this.punishment.getCreatedAt()));
            lore.add(Color.SECONDARY_COLOR + "Punish Reason: " + Color.MAIN_COLOR + this.punishment.getReason());
            lore.add(Color.MAIN_COLOR + ChatColor.STRIKETHROUGH.toString() + "------------------------------------");
            lore.add(Color.SECONDARY_COLOR + "Punish Type: " + Color.MAIN_COLOR + this.punishment.getPunishmentType().getName());
            lore.add(Color.SECONDARY_COLOR + "Punish Expiring: " + Color.MAIN_COLOR + this.punishment.getExpirationString());
            lore.add(Color.MAIN_COLOR + ChatColor.STRIKETHROUGH.toString() + "------------------------------------");

            if (this.punishment.isRemoved()) {
                lore.add(Color.SECONDARY_COLOR + "Removed By: " + Color.MAIN_COLOR + (this.punishment.getRemover() != null ? (this.punishment.getRemoverName().equals("Console") ? ChatColor.DARK_RED + "Console" : PunishViewPaginatedMenu.this.target) : ChatColor.DARK_RED + "Console"));
                lore.add(Color.SECONDARY_COLOR + "Removed Reason: " + Color.MAIN_COLOR + this.punishment.getRemovalReason());
                lore.add(Color.MAIN_COLOR + ChatColor.STRIKETHROUGH.toString() + "------------------------------------");
            }

            return new ItemBuilder(XMaterial.LIME_WOOL.parseMaterial(), this.punishment.isActive() ? 5 : (this.punishment.isRemoved() ? 1 : 14))
                    .setDisplayName(statusLore + " " + ChatColor.BOLD.toString() + CorePlugin.FORMAT.format(this.punishment.getIssuingDate()))
                    .addLore(lore).create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.equals(ClickType.RIGHT)) {
                new PunishRemoveConfirmMenu(player, PunishViewPaginatedMenu.this.target, this.punishment).open(player);
                PunishViewPaginatedMenu.this.setClosedByMenu(true);
            }
        }
    }
}
