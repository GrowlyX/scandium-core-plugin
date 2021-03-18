package com.solexgames.core.util.external.pagination.extend;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.extend.punish.remove.PunishRemoveConfirmMenu;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.UUIDUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.Getter;
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
        super(45);
        this.player = player;
        this.target = target;
        this.punishmentType = punishmentType;
        this.targetUuid = UUIDUtil.fetchUUID(target);
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Applicable punishments of: " + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        AtomicInteger i = new AtomicInteger(0);

        getSortedPunishmentsByType().forEach(punishment -> {
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
            lore.add("&ePunish On: &b" + network.getMainColor() + CorePlugin.FORMAT.format(punishment.getCreatedAt()));
            lore.add("&ePunish Reason: &b" + network.getMainColor() + punishment.getReason());
            lore.add(network.getMainColor() + "&m------------------------------------");
            lore.add("&ePunish Type: &b" + network.getMainColor() + punishment.getPunishmentType().getName());
            lore.add("&ePunish Status: &b" + network.getMainColor() + statusLore);
            lore.add("&ePunish Expiring: &b" + network.getMainColor() + punishment.getExpirationString());
            lore.add(network.getMainColor() + "&m------------------------------------");

            if (punishment.isRemoved()) {
                lore.add("&eRemoved By: &b" + network.getMainColor() + (punishment.getRemoverName() != null ? (punishment.getRemoverName().equals("Console") ? "&4Console" : UUIDUtil.fetchName(punishment.getRemover())) : "Not recorded"));
                lore.add("&eRemoved Reason: &b" + network.getMainColor() + punishment.getRemovalReason());
                lore.add(network.getMainColor() + "&m------------------------------------");
            }

            buttons.put(i.get(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(XMaterial.LIME_WOOL.parseMaterial(), (punishment.isActive() ? 5 : (punishment.isRemoved() ? 8 : 14)))
                            .setDisplayName(ChatColor.RED + "#" + punishment.getPunishIdentification())
                            .addLore(Color.translate(lore))
                            .create();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    if (clickType.equals(ClickType.RIGHT)) {
                        String display = ChatColor.stripColor(Color.translate(getButtonItem(player).getItemMeta().getDisplayName()));
                        String id = display.replace("#", "");
                        Punishment punishment = Punishment.getByIdentification(id);

                        if (punishment != null) {
                            new PunishRemoveConfirmMenu(player, target, punishment).open(player);
                        }
                    }
                }
            });

            i.getAndIncrement();
        });

        return buttons;
    }

    private List<Punishment> getSortedPunishmentsByType() {
        return Punishment.getAllPunishments().stream()
                .filter(Objects::nonNull)
                .filter(punishment -> punishment.getPunishmentType() == this.punishmentType)
                .filter(punishment -> punishment.getTarget().equals(this.getTargetUuid()))
                .sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed())
                .collect(Collectors.toList());
    }
}
