package com.solexgames.core.util.external.impl.editor;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.Menu;
import com.solexgames.core.util.prompt.editor.RankColorPrompt;
import com.solexgames.core.util.prompt.editor.RankPrefixPrompt;
import com.solexgames.core.util.prompt.editor.RankSuffixPrompt;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GrowlyX
 * @since 5/18/2021
 */

@RequiredArgsConstructor
public class RankEditorEditMenu extends Menu {

    private final Rank rank;

    @Override
    public String getTitle(Player player) {
        return "Editing a rank: " + this.rank.getColor() + this.rank.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new ItemBuilder(XMaterial.WHITE_WOOL.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Modify color")
                .addLore(
                        "&7Modify the rank's display",
                        "&7name color!",
                        "",
                        "&7Current: " + this.rank.getColor() + "Example",
                        "",
                        "&e[Click to start modifying]"
                )
                .toButton((player1, clickType) -> {
                    final Conversation conversation = CorePlugin.getInstance().getConversationFactory()
                            .withFirstPrompt(new RankColorPrompt(player, this.rank))
                            .withLocalEcho(false)
                            .buildConversation(player);

                    conversation.begin();

                    player.closeInventory();
                })
        );

        buttons.put(1, new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Modify prefix")
                .addLore(
                        "&7Modify the rank's prefix!",
                        "",
                        "&7Current: " + Color.SECONDARY_COLOR + this.rank.getPrefix(),
                        "",
                        "&e[Click to start modifying]"
                )
                .toButton((player1, clickType) -> {
                    final Conversation conversation = CorePlugin.getInstance().getConversationFactory()
                            .withFirstPrompt(new RankPrefixPrompt(player, this.rank))
                            .withLocalEcho(false)
                            .buildConversation(player);

                    conversation.begin();

                    player.closeInventory();
                })
        );

        buttons.put(2, new ItemBuilder(XMaterial.OAK_DOOR.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Modify suffix")
                .addLore(
                        "&7Modify the rank's suffix!",
                        "",
                        "&7Current: " + Color.SECONDARY_COLOR + this.rank.getSuffix(),
                        "",
                        "&e[Click to start modifying]"
                )
                .toButton((player1, clickType) -> {
                    final Conversation conversation = CorePlugin.getInstance().getConversationFactory()
                            .withFirstPrompt(new RankSuffixPrompt(player, this.rank))
                            .withLocalEcho(false)
                            .buildConversation(player);

                    conversation.begin();

                    player.closeInventory();
                })
        );

        buttons.put(3, new ItemBuilder(XMaterial.ANVIL.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Modify weight")
                .addLore(
                        "&7Modify the rank's weight!",
                        "",
                        "&7The rank's weight will apply",
                        "&7on the tab-list, list command,",
                        "&7granting, punishments, and more!",
                        "",
                        "&7Current: " + Color.SECONDARY_COLOR + this.rank.getWeight(),
                        "",
                        "&e[Right-click to decrement by 10]",
                        "&e[Left-click to increment by 10]"
                )
                .toUpdatingButton((player1, clickType) -> {
                    if (clickType.name().contains("RIGHT")) {
                        this.rank.setWeight(this.rank.getWeight() - 10);
                    } else if (clickType.name().contains("LEFT")) {
                        this.rank.setWeight(this.rank.getWeight() + 10);
                    }
                })
        );

        buttons.put(4, new ItemBuilder(XMaterial.PISTON.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Modify hiding")
                .addLore(
                        "&7Modify the rank's hidden mode!",
                        "",
                        "&7Current: " + Color.SECONDARY_COLOR + this.rank.isHidden(),
                        "",
                        "&e[Click to toggle hidden mode]"
                )
                .toUpdatingButton((player1, clickType) -> this.rank.setHidden(!this.rank.isHidden()))
        );

        buttons.put(5, new ItemBuilder(XMaterial.EGG.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Modify default")
                .addLore(
                        "&7Modify the rank's default mode!",
                        "",
                        "&c&lNOTE: &7Make sure to only have",
                        "&7one default rank or there will be",
                        "&7a ton of problems! Also make sure",
                        "&7to restart after creating a new",
                        "&7default rank.",
                        "",
                        "&7Current: " + Color.SECONDARY_COLOR + this.rank.isDefaultRank(),
                        "",
                        "&e[Click to toggle default mode]"
                )
                .toUpdatingButton((player1, clickType) -> this.rank.setDefaultRank(!this.rank.isDefaultRank()))
        );

        buttons.put(6, new ItemBuilder(XMaterial.EXPERIENCE_BOTTLE.parseMaterial())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Modify purchasable")
                .addLore(
                        "&7Modify the rank's purchasable mode!",
                        "",
                        "&7Current: " + Color.SECONDARY_COLOR + this.rank.isPurchasable(),
                        "",
                        "&e[Click to toggle purchasable mode]"
                )
                .toUpdatingButton((player1, clickType) -> this.rank.setPurchasable(!this.rank.isPurchasable()))
        );

        buttons.put(8, new ItemBuilder(XMaterial.RED_BED.parseMaterial())
                .setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Return Home")
                .addLore(
                        "&7Return back to the main",
                        "&7rank editor menu.",
                        " ",
                        "&7If you leave, the rank",
                        "&7will be automatically",
                        "&7saved.",
                        "",
                        "&e[Click to return home]"
                )
                .toButton((player1, clickType) -> {
                    this.rank.saveRank();
                    RedisUtil.publishAsync(RedisUtil.updateRank(this.rank));

                    new RankEditorMainMenu().openMenu(player);
                }));

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        this.rank.saveRank();
        RedisUtil.publishAsync(RedisUtil.updateRank(this.rank));
    }
}
