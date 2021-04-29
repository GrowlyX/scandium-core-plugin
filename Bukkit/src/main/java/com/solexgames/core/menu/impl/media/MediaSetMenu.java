package com.solexgames.core.menu.impl.media;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.prompt.GrantDurationPrompt;
import com.solexgames.core.util.prompt.SocialMediaPrompt;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class MediaSetMenu extends AbstractInventoryMenu {

    private Player player;

    public MediaSetMenu(Player player) {
        super("Social Media", 9);
        this.player = player;
        this.update();
    }

    public void update() {
        this.inventory.setItem(2, new ItemBuilder(XMaterial.BLUE_DYE.parseMaterial())
                .setDurability(4)
                .setDisplayName("&9Discord")
                .addLore(
                        "&7Click here to setup",
                        "&7your discord account!"
                )
                .create()
        );
        this.inventory.setItem(3, new ItemBuilder(XMaterial.RED_DYE.parseMaterial())
                .setDurability(1)
                .setDisplayName("&cYouTube")
                .addLore(
                        "&7Click here to setup",
                        "&7your youtube account!"
                )
                .create()
        );
        this.inventory.setItem(4, new ItemBuilder(XMaterial.LIGHT_BLUE_DYE.parseMaterial())
                .setDurability(12)
                .setDisplayName("&bTwitter")
                .addLore(
                        "&7Click here to setup",
                        "&7your twitter account!"
                )
                .create()
        );
        this.inventory.setItem(5, new ItemBuilder(XMaterial.ORANGE_DYE.parseMaterial())
                .setDurability(14)
                .setDisplayName("&6Instagram")
                .addLore(
                        "&7Click here to setup",
                        "&7your insta account!"
                )
                .create()
        );
        this.inventory.setItem(6, new ItemBuilder(XMaterial.YELLOW_DYE.parseMaterial())
                .setDurability(11)
                .setDisplayName("&e???")
                .addLore(
                        "&7Click here to setup",
                        "&7your ??? account!"
                )
                .create()
        );
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) return;

            Conversation conversation = null;

            switch (event.getRawSlot()) {
                case 2:
                    conversation = CorePlugin.getInstance().getConversationFactory()
                            .withFirstPrompt(new SocialMediaPrompt(player, "discord", ChatColor.BLUE + "Discord"))
                            .withLocalEcho(false)
                            .buildConversation(player);
                    break;
                case 3:
                    conversation = CorePlugin.getInstance().getConversationFactory()
                            .withFirstPrompt(new SocialMediaPrompt(player, "youtube", ChatColor.RED + "YouTube"))
                            .withLocalEcho(false)
                            .buildConversation(player);
                    break;
                case 4:
                    conversation = CorePlugin.getInstance().getConversationFactory()
                            .withFirstPrompt(new SocialMediaPrompt(player, "twitter", ChatColor.AQUA + "Twitter"))
                            .withLocalEcho(false)
                            .buildConversation(player);
                    break;
                case 5:
                    conversation = CorePlugin.getInstance().getConversationFactory()
                            .withFirstPrompt(new SocialMediaPrompt(player, "instagram", ChatColor.LIGHT_PURPLE + "Instagram"))
                            .withLocalEcho(false)
                            .buildConversation(player);
                    break;
                case 6:
                    player.sendMessage(ChatColor.RED + ("This button is currently disabled."));
                    break;
            }

            if (conversation != null) {
                conversation.begin();
            }

            player.closeInventory();
        }
    }
}
