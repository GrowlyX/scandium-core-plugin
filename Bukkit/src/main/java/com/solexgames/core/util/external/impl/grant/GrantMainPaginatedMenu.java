package com.solexgames.core.util.external.impl.grant;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.WoolUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class GrantMainPaginatedMenu extends PaginatedMenu {

    private final Document document;
    private final Player player;

    public GrantMainPaginatedMenu(Document document, Player player) {
        super(45);
        this.document = document;
        this.player = player;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        final Rank rank = Rank.getByName(document.getString("rank"));
        final String formatted = rank == null ? ChatColor.GRAY.toString() : rank.getColor();
        final String name = formatted + document.getString("name");

        return "Grant for: " + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : name);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();
        final AtomicInteger i = new AtomicInteger();
        final  ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        CorePlugin.getInstance().getRankManager().getSortedRanksFromTop().forEach(rank -> buttons.put(i.getAndIncrement(), new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_WOOL.parseMaterial(), ((rank.getColor() != null) ? (ChatColor.getByChar(Color.translate(rank.getColor().replace("&", "").replace("§", ""))) != null) ? WoolUtil.getByColor(ChatColor.getByChar(Color.translate(rank.getColor().replace("&", "").replace("§", "")))) : 0 : 0))
                        .addLore(Arrays.asList(
                                network.getMainColor() + "&m--------------------------------",
                                network.getSecondaryColor() + "Priority: " + network.getMainColor() + rank.getWeight(),
                                network.getSecondaryColor() + "Prefix: " + network.getMainColor() + rank.getPrefix(),
                                network.getSecondaryColor() + "Suffix: " + network.getMainColor() + rank.getSuffix(),
                                network.getSecondaryColor() + "Visible: " + network.getMainColor() + rank.isHidden(),
                                network.getSecondaryColor() + "Color: " + network.getMainColor() + rank.getColor() + rank.getItalic() + "Example",
                                "",
                                ChatColor.GREEN + "Left-Click to grant the " + rank.getColor() + rank.getItalic() + rank.getName() + ChatColor.GREEN + " rank!",
                                ChatColor.GREEN + "Right-Click to grant with scope selection.",
                                network.getMainColor() + "&m--------------------------------"
                        ))
                        .setDisplayName(rank.getColor() + rank.getItalic() + rank.getName())
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                String display = ChatColor.stripColor(Color.translate(getButtonItem(player).getItemMeta().getDisplayName()));
                Rank rank = Rank.getByName(display);
                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                if (clickType == ClickType.RIGHT) {
                    if (rank != null) {
                        if ((potPlayer.getActiveGrant().getRank().getWeight() >= rank.getWeight()) && !player.isOp()) {
                            new GrantScopePaginatedMenu(player, document, rank).openMenu(player);
                        } else if ((potPlayer.getActiveGrant().getRank().getWeight() >= rank.getWeight()) && player.isOp()) {
                            new GrantScopePaginatedMenu(player, document, rank).openMenu(player);
                        } else {
                            player.sendMessage(ChatColor.RED + ("You cannot grant a rank weight a weight that is higher than yours."));
                            player.closeInventory();
                        }
                    }
                } else if (clickType == ClickType.LEFT) {
                    if (rank != null) {
                        if ((potPlayer.getActiveGrant().getRank().getWeight() >= rank.getWeight()) && !player.isOp()) {
                            new GrantDurationPaginatedMenu(player, getDocument(), rank, "global").openMenu(player);
                        } else if ((potPlayer.getActiveGrant().getRank().getWeight() >= rank.getWeight()) && player.isOp()) {
                            new GrantDurationPaginatedMenu(player, getDocument(), rank, "global").openMenu(player);
                        } else {
                            player.sendMessage(ChatColor.RED + ("You cannot grant a rank weight a weight that is higher than yours."));
                            player.closeInventory();
                        }
                    }
                }
            }
        }));

        return buttons;
    }
}
