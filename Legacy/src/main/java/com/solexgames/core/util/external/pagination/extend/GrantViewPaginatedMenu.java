package com.solexgames.core.util.external.pagination.extend;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.impl.grant.remove.GrantRemoveConfirmMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
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
    private final Document target;

    private List<Grant> allGrants;

    public GrantViewPaginatedMenu(Player player, Document target) {
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
        return "Applicable grants for: " + (Rank.getByName(target.getString("rankName")) != null ? Rank.getByName(target.getString("rankName")).getColor() : ChatColor.GRAY) + target.getString("name");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        AtomicInteger i = new AtomicInteger(0);
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        if ((((List<String>) target.get("allGrants")).isEmpty()) || (target.get("allGrants") == null)) {
            allGrants.add(new Grant(null, Objects.requireNonNull(Rank.getDefault()), new Date().getTime(), -1L, "Automatic Grant (Default)", true, true));
        } else {
            List<String> allGrants = ((List<String>) target.get("allGrants"));
            allGrants.forEach(s -> this.allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
        }

        this.allGrants.stream().sorted(Comparator.comparingLong(Grant::getDateAdded).reversed()).forEach(grant -> buttons.put(i.getAndIncrement(), new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> arrayList = new ArrayList<>();

                arrayList.add(network.getMainColor() + "&m------------------------------------");
                arrayList.add("&eTarget&7: " + network.getMainColor() + target.getString("name"));
                arrayList.add("&eRank&7: " + network.getMainColor() + grant.getRank().getColor() + grant.getRank().getName());
                arrayList.add("&eDuration&7: " + network.getMainColor() + (grant.isPermanent() ? "&4Forever" : DurationFormatUtils.formatDurationWords(grant.getDuration(), true, true)));
                arrayList.add(network.getMainColor() + "&m------------------------------------");
                arrayList.add(network.getSecondaryColor() + "Scopes:");
                arrayList.add(" &7- " + ChatColor.GREEN + (grant.getScope() != null ? grant.getScope() : "global"));
                arrayList.add(network.getMainColor() + "&m------------------------------------");
                arrayList.add("&eIssued By&7: " + network.getMainColor() + (grant.getIssuer() != null ? Bukkit.getOfflinePlayer(grant.getIssuer()).getName() : "&4Console"));
                arrayList.add("&eIssued On&7: " + network.getMainColor() + CorePlugin.FORMAT.format(new Date(grant.getDateAdded())));
                arrayList.add("&eIssued At&7: " + network.getMainColor() + (grant.getIssuedServer() != null ? grant.getIssuedServer() : "Not Recorded"));
                arrayList.add("&eIssued Reason&7: " + network.getMainColor() + grant.getReason());
                arrayList.add(network.getMainColor() + "&m------------------------------------");

                return new ItemBuilder(XMaterial.LIME_WOOL.parseMaterial(), (grant.isActive() ? 5 : (grant.isExpired() ? 8 : 14)))
                        .setDisplayName(ChatColor.RED + "#" + grant.getId())
                        .addLore(arrayList)
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                if (clickType.equals(ClickType.RIGHT)) {
                    String display = ChatColor.stripColor(getButtonItem(player).getItemMeta().getDisplayName());
                    String id = display.replace("#", "");

                    allGrants.stream().filter(grant1 -> grant1.getId().equalsIgnoreCase(id)).findFirst()
                            .ifPresent(grant -> new GrantRemoveConfirmMenu(player, target, grant, allGrants).open(player));
                }
            }
        }));

        return buttons;
    }
}
