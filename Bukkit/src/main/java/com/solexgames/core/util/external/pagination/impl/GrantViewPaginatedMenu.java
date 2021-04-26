package com.solexgames.core.util.external.pagination.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.impl.grant.GrantRemoveConfirmMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GrantViewPaginatedMenu extends PaginatedMenu {

    private final Player player;
    private final Player target;

    public GrantViewPaginatedMenu(Player player, Player target) {
        super(18);

        this.player = player;
        this.target = target;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Applicable grants for: " + target.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();
        final AtomicInteger atomicInteger = new AtomicInteger();
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

        potPlayer.getAllGrants().forEach(grant -> buttons.put(atomicInteger.getAndIncrement(), new GrantButton(grant)));

        return buttons;
    }

    @AllArgsConstructor
    private class GrantButton extends Button {

        private final Grant grant;

        @Override
        public ItemStack getButtonItem(Player player) {
            final List<String> arrayList = new ArrayList<>();
            final String statusLore = this.grant.isActive() ? ChatColor.GREEN + "[Active]" : (this.grant.isExpired() ? ChatColor.GOLD + "[Expired]" : ChatColor.RED + "[Removed]");

            arrayList.add(Color.MAIN_COLOR + "&m------------------------------------");
            arrayList.add(Color.SECONDARY_COLOR + "Target&7: " + Color.MAIN_COLOR + target.getDisplayName());
            arrayList.add(Color.SECONDARY_COLOR + "Rank&7: " + Color.MAIN_COLOR + this.grant.getRank().getColor() + this.grant.getRank().getName());
            arrayList.add(Color.SECONDARY_COLOR + "Duration&7: " + Color.MAIN_COLOR + (this.grant.isPermanent() ? "&4Forever" : DurationFormatUtils.formatDurationWords(this.grant.getDuration(), true, true)));
            arrayList.add(Color.MAIN_COLOR + "&m------------------------------------");
            arrayList.add(Color.SECONDARY_COLOR + "Scopes:");
            arrayList.add(" &7- " + ChatColor.GREEN + Color.translate(this.grant.getScope() != null ? this.grant.getScope() : "global"));
            arrayList.add(Color.MAIN_COLOR + "&m------------------------------------");
            arrayList.add(Color.SECONDARY_COLOR + "Issued By&7: " + Color.MAIN_COLOR + (this.grant.getIssuer() != null ? Bukkit.getOfflinePlayer(this.grant.getIssuer()).getName() : "&4Console"));
            arrayList.add(Color.SECONDARY_COLOR + "Issued On&7: " + Color.MAIN_COLOR + CorePlugin.FORMAT.format(new Date(this.grant.getDateAdded())));
            arrayList.add(Color.SECONDARY_COLOR + "Issued At&7: " + Color.MAIN_COLOR + (this.grant.getIssuedServer() != null ? this.grant.getIssuedServer() : "Not Recorded"));
            arrayList.add(Color.SECONDARY_COLOR + "Issued Reason&7: " + Color.MAIN_COLOR + this.grant.getReason());
            arrayList.add(Color.MAIN_COLOR + "&m------------------------------------");

            return new ItemBuilder(XMaterial.LIME_WOOL.parseMaterial(), (this.grant.isActive() ? (grant.getScope().equals("global") ? 5 : 13) : (this.grant.isExpired() ? 1 : 14)))
                    .setDisplayName(statusLore + " " + CorePlugin.FORMAT.format(new Date(this.grant.getDateAdded())))
                    .addLore(arrayList)
                    .create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.equals(ClickType.RIGHT)) {
                new GrantRemoveConfirmMenu(player, target, this.grant).open(player);
                setClosedByMenu(true);
            }
        }
    }
}
