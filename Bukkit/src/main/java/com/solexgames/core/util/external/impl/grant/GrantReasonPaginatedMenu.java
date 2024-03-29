package com.solexgames.core.util.external.impl.grant;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.impl.grant.GrantSelectConfirmMenu;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import com.solexgames.core.util.prompt.GrantReasonPrompt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GrantReasonPaginatedMenu extends PaginatedMenu {

    private final Player player;
    private final Document document;
    private final Rank rank;
    private final long duration;
    private final boolean permanent;
    private final String scope;

    public GrantReasonPaginatedMenu(Player player, Document document, long duration, Rank rank, boolean permanent, String scope) {
        super(9);

        this.player = player;
        this.document = document;
        this.rank = rank;
        this.duration = duration;
        this.permanent = permanent;
        this.scope = scope;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        buttonMap.put(2, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseMaterial())
                        .setDurability(5)
                        .setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Custom Reason")
                        .addLore(
                                ChatColor.GRAY + "Click to choose a custom",
                                ChatColor.GRAY + "reason for your grant!"
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                final Conversation conversation = CorePlugin.getInstance().getConversationFactory()
                        .withFirstPrompt(new GrantReasonPrompt(player, document, rank, duration, scope, permanent))
                        .withLocalEcho(false)
                        .buildConversation(player);

                conversation.begin();

                player.closeInventory();
            }
        });

        buttonMap.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_BED.parseMaterial())
                        .setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Return Back")
                        .addLore(
                                ChatColor.GRAY + "Click to return to the",
                                ChatColor.GRAY + "previous menu!"
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new GrantDurationPaginatedMenu(player, document, rank, scope).openMenu(player);
            }
        });

        buttonMap.put(6, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(XMaterial.RED_TERRACOTTA.parseMaterial())
                        .setDurability(14)
                        .setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Default Reason")
                        .addLore(
                                ChatColor.GRAY + "Click to select the default",
                                ChatColor.GRAY + "reason for this grant."
                        )
                        .create();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new ReasonButton(XMaterial.PAPER, 0, "Unspecified").clicked(player, clickType);
            }
        });

        return buttonMap;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Grant reason for: " + (Bukkit.getPlayer(document.getString("name")) != null ? Bukkit.getPlayer(document.getString("name")).getDisplayName() : document.getString("name"));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        buttonMap.put(0, new ReasonButton(XMaterial.WHITE_WOOL, 0, "Rank Migration"));
        buttonMap.put(1, new ReasonButton(XMaterial.ORANGE_WOOL, 1, "Buycraft Issues"));
        buttonMap.put(2, new ReasonButton(XMaterial.PINK_WOOL, 2, "Promotion"));
        buttonMap.put(3, new ReasonButton(XMaterial.LIGHT_BLUE_WOOL, 3, "Demotion"));
        buttonMap.put(4, new ReasonButton(XMaterial.YELLOW_WOOL, 4, "Giveaway Winner"));
        buttonMap.put(5, new ReasonButton(XMaterial.GREEN_WOOL, 5, "Event Winner"));

        return buttonMap;
    }

    @AllArgsConstructor
    public class ReasonButton extends Button {

        private final XMaterial material;
        private final int data;
        private final String reason;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(this.material.parseMaterial())
                    .setDisplayName(Color.MAIN_COLOR + this.reason)
                    .addLore(ChatColor.GRAY + "Click to choose this reason.")
                    .setDurability(this.data)
                    .create();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new GrantSelectConfirmMenu(player, GrantReasonPaginatedMenu.this.getDocument(), GrantReasonPaginatedMenu.this.getRank(), GrantReasonPaginatedMenu.this.getDuration(), this.reason, GrantReasonPaginatedMenu.this.isPermanent(), GrantReasonPaginatedMenu.this.getScope()).open(player);
        }
    }
}
