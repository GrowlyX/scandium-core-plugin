package com.solexgames.core.util.external.pagination.impl;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.impl.grant.GrantRemoveConfirmMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
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
        super(45);

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
        HashMap<Integer, Button> buttons = new HashMap<>();
        AtomicInteger i = new AtomicInteger();
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

        potPlayer.getAllGrants().forEach(grant -> buttons.put(i.getAndIncrement(), new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> arrayList = new ArrayList<>();
                String statusLore = grant.isActive() ? ChatColor.GREEN + "[Active]" : (grant.isExpired() ? ChatColor.GOLD + "[Expired]" : ChatColor.RED + "[Removed]");

                arrayList.add(network.getMainColor() + "&m------------------------------------");
                arrayList.add("&eTarget&7: " + network.getMainColor() + target.getDisplayName());
                arrayList.add("&eRank&7: " + network.getMainColor() + grant.getRank().getColor() + grant.getRank().getName());
                arrayList.add("&eDuration&7: " + network.getMainColor() + (grant.isPermanent() ? "&4Forever" : DurationFormatUtils.formatDurationWords(grant.getDuration(), true, true)));
                arrayList.add(network.getMainColor() + "&m------------------------------------");
                arrayList.add(network.getSecondaryColor() + "Scopes:");
                arrayList.add(" &7- " + ChatColor.GREEN + Color.translate(grant.getScope() != null ? grant.getScope() : "global"));
                arrayList.add(network.getMainColor() + "&m------------------------------------");
                arrayList.add("&eIssued By&7: " + network.getMainColor() + (grant.getIssuer() != null ? Bukkit.getOfflinePlayer(grant.getIssuer()).getName() : "&4Console"));
                arrayList.add("&eIssued On&7: " + network.getMainColor() + CorePlugin.FORMAT.format(new Date(grant.getDateAdded())));
                arrayList.add("&eIssued At&7: " + network.getMainColor() + (grant.getIssuedServer() != null ? grant.getIssuedServer() : "Not Recorded"));
                arrayList.add("&eIssued Reason&7: " + network.getMainColor() + grant.getReason());
                arrayList.add(network.getMainColor() + "&m------------------------------------");

                return new ItemBuilder(XMaterial.LIME_WOOL.parseMaterial(), (grant.isActive() ? 5 : (grant.isExpired() ? 1 : 14)))
                        .setDisplayName(ChatColor.DARK_GRAY + "#" + grant.getId() + " " + statusLore)
                        .addLore(arrayList)
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                if (clickType.equals(ClickType.RIGHT)) {
                    new GrantRemoveConfirmMenu(player, target, grant).open(player);
                    setClosedByMenu(true);
                }
            }
        }));

        return buttons;
    }
}
