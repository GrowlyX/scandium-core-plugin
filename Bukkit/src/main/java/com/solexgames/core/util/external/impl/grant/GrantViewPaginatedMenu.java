package com.solexgames.core.util.external.impl.grant;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import com.solexgames.core.util.prompt.GrantRemovalPrompt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class GrantViewPaginatedMenu extends PaginatedMenu {

    private final Document document;
    private final String fancyName;

    public GrantViewPaginatedMenu(Document target) {
        super(18);

        this.document = target;

        final Rank rank = Rank.getByName(target.getString("rankName"));
        this.fancyName = (rank != null ? rank.getColor() + rank.getItalic() : ChatColor.GRAY.toString()) + target.getString("name");
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Applicable grants for: " + this.fancyName;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();
        final AtomicInteger atomicInteger = new AtomicInteger();
        final List<Grant> grantList = this.document.getList("allGrants", String.class).stream()
                .map(grant -> CorePlugin.GSON.fromJson(grant, Grant.class))
                .filter(Objects::nonNull).collect(Collectors.toList());

        grantList.forEach(grant -> buttons.put(atomicInteger.getAndIncrement(), new GrantButton(grant)));

        return buttons;
    }

    @AllArgsConstructor
    private class GrantButton extends Button {

        private final Grant grant;

        @Override
        public ItemStack getButtonItem(Player player) {
            final List<String> arrayList = new ArrayList<>();
            final String statusLore = this.grant.isRemoved() ? ChatColor.RED + "[Removed]" : this.grant.isActive() ? ChatColor.GREEN + "[Active]" : (this.grant.isExpired() ? ChatColor.GOLD + "[Expired]" : ChatColor.RED + "[Removed]");

            arrayList.add(Color.MAIN_COLOR + "&m------------------------------------");
            arrayList.add(Color.SECONDARY_COLOR + "Target&7: " + Color.MAIN_COLOR + fancyName);
            arrayList.add(Color.SECONDARY_COLOR + "Rank&7: " + Color.MAIN_COLOR + (this.grant.getRank() == null ? "" : this.grant.getRank().getColor() + this.grant.getRank().getName()));
            arrayList.add(Color.SECONDARY_COLOR + "Duration&7: " + Color.MAIN_COLOR + (this.grant.isPermanent() ? "&4Forever" : DurationFormatUtils.formatDurationWords(this.grant.getDuration(), true, true)));
            arrayList.add(Color.SECONDARY_COLOR + "Expires On&7: " + Color.MAIN_COLOR + CorePlugin.FORMAT.format(new Date(this.grant.getDateAdded() + this.grant.getDuration())));
            arrayList.add(Color.MAIN_COLOR + "&m------------------------------------");
            arrayList.add(Color.SECONDARY_COLOR + "Scopes:");
            arrayList.add(" &7- " + ChatColor.GREEN + (this.grant.getScope() != null ? this.grant.getScope() : "global"));
            arrayList.add(Color.MAIN_COLOR + "&m------------------------------------");
            arrayList.add(Color.SECONDARY_COLOR + "Issued By&7: " + Color.MAIN_COLOR + (this.grant.getIssuer() != null ? Bukkit.getOfflinePlayer(this.grant.getIssuer()).getName() : "&4Console"));
            arrayList.add(Color.SECONDARY_COLOR + "Issued On&7: " + Color.MAIN_COLOR + (this.grant.getIssuedServer() != null ? this.grant.getIssuedServer() : "Not Recorded"));
            arrayList.add(Color.SECONDARY_COLOR + "Issued Reason&7: " + Color.MAIN_COLOR + this.grant.getReason());
            arrayList.add(Color.MAIN_COLOR + "&m------------------------------------");

            if (!grant.isExpired()) {
                if (!this.grant.isRemoved()) {
                    arrayList.add(CorePlugin.getInstance().getPlayerManager().getPlayer(player).getActiveGrant().getRank().getWeight() > this.grant.getRank().getWeight() || player.isOp() ? ChatColor.GREEN + "Right-click to remove this grant!" : ChatColor.RED + "You don't have permission to remove this grant!");
                } else {
                    arrayList.add(Color.SECONDARY_COLOR + "Removed By&7: " + Color.MAIN_COLOR + this.grant.getRemovedBy());
                    arrayList.add(Color.SECONDARY_COLOR + "Removed Reason&7: " + Color.MAIN_COLOR + this.grant.getRemovedFor());
                }

                arrayList.add(Color.MAIN_COLOR + "&m------------------------------------");
            }

            return new ItemBuilder(XMaterial.LIME_WOOL.parseMaterial(), (grant.isRemoved() ? 14 : (this.grant.isActive() ? (grant.getScope().equals("global") ? 5 : 13) : (this.grant.isExpired() ? 1 : 14))))
                    .setDisplayName(statusLore + ChatColor.BOLD.toString() + " " + CorePlugin.FORMAT.format(new Date(this.grant.getDateAdded())))
                    .addLore(arrayList)
                    .create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.equals(ClickType.RIGHT) ) {
                if (CorePlugin.getInstance().getPlayerManager().getPlayer(player).getActiveGrant().getRank().getWeight() < this.grant.getRank().getWeight() || !player.isOp()) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to remove this grant.");
                    player.closeInventory();
                    return;
                }

                if (this.grant.isRemoved()) {
                    player.sendMessage(ChatColor.RED + "This grant has already been removed.");
                    player.closeInventory();
                    return;
                }

                if (this.grant.isExpired() && !this.grant.isPermanent()) {
                    player.sendMessage(ChatColor.RED + "This grant has already expired.");
                    player.closeInventory();
                    return;
                }

                final Conversation conversation = CorePlugin.getInstance().getConversationFactory()
                        .withFirstPrompt(new GrantRemovalPrompt(this.grant, player, document, fancyName))
                        .withLocalEcho(false)
                        .buildConversation(player);

                conversation.begin();
                setClosedByMenu(true);

                player.closeInventory();
            }
        }
    }
}
