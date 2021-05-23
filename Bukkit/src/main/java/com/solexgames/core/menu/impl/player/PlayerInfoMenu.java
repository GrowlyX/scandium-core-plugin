package com.solexgames.core.menu.impl.player;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.menu.impl.ReportMenu;
import com.solexgames.core.menu.impl.media.MediaViewMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.impl.network.NetworkServerMainMenu;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author GrowlyX
 * @since 5/20/2021
 */

public class PlayerInfoMenu extends AbstractInventoryMenu {

    private final Player player;

    public PlayerInfoMenu(Player player) {
        super("Profile » " + player.getName(), 27);
        this.player = player;

        this.update();
    }

    @Override
    public void update() {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);
        final boolean currentlyDisguised = potPlayer.isDisguised();
        final Rank rank = currentlyDisguised ? potPlayer.getDisguiseRank() : potPlayer.getActiveGrant().getRank();

        this.inventory.setItem(11, new ItemBuilder(Material.SKULL_ITEM)
                .setDurability(3)
                .setOwner(this.player.getName())
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD + this.player.getName() + "'s Profile")
                .addLore(
                        "&7Rank: &f" + rank.getColor() + rank.getItalic() + rank.getName(),
                        "",
                        "&7First joined: &f" + potPlayer.getFirstJoin(),
                        "&7Last seen: &f" + CorePlugin.FORMAT.format(potPlayer.getLastJoined()),
                        "",
                        "&7Experience: &f" + Color.SECONDARY_COLOR + potPlayer.getExperience(),
                        "&7Prefix: &f" + Color.SECONDARY_COLOR + (currentlyDisguised ? "None" : potPlayer.getAppliedPrefix() == null ? "None" : potPlayer.getAppliedPrefix().getName()),
                        "&7Chat Color: &f" + Color.SECONDARY_COLOR + (currentlyDisguised ? "None" : potPlayer.getCustomColor() == null ? "None" : potPlayer.getCustomColor() + StringUtils.upperCase(potPlayer.getCustomColor().name().replace("_", " ").toLowerCase())),
                        "",
                        "&7Status: &f" + this.getStatusByPlayer(potPlayer),
                        "&7Synced: &f" + (potPlayer.isSynced() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No")
                )
                .create());

        this.inventory.setItem(13, new ItemBuilder(Material.BOOK_AND_QUILL)
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD + "Report")
                .addLore(
                        "&7If you believe that " + Color.SECONDARY_COLOR + this.player.getName(),
                        "&7has broken the rules, you",
                        "&7can report them here.",
                        "",
                        "&7Please do not submit false",
                        "&7reports, or you will be",
                        "&4punished&7.",
                        "",
                        "&e[Click to report player]"
                )
                .create());

        this.inventory.setItem(15, new ItemBuilder(Material.SKULL_ITEM)
                .setDurability(3)
                .setOwner("nek0skeppy")
                .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD + "Social Media")
                .addLore(
                        "&7If " + Color.SECONDARY_COLOR + this.player.getName() + ChatColor.GRAY + " has",
                        "&7configured their social",
                        "&7media profile, you will be",
                        "&7able to view them below.",
                        "",
                        "&7Twitter: &b" + (currentlyDisguised ? "N/A" : potPlayer.getMedia().getTwitter()),
                        "&7Discord: &9" + (currentlyDisguised ? "N/A" : potPlayer.getMedia().getDiscord()),
                        "",
                        "&7Instagram: &6" + (currentlyDisguised ? "N/A" : potPlayer.getMedia().getInstagram()),
                        "&7YouTube: &c" + (currentlyDisguised ? "N/A" : potPlayer.getMedia().getYoutubeLink()),
                        "",
                        "&e[Click to open full menu]"
                )
                .create());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        final Inventory topInventory = event.getView().getTopInventory();

        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            final ItemStack item = event.getCurrentItem();
            final Player player = (Player) event.getWhoClicked();

            if (item == null || item.getType() == XMaterial.AIR.parseMaterial())
                return;

            if (event.getRawSlot() == 15) {
                new MediaViewMenu(this.player).open(player);
            }
            if (event.getRawSlot() == 13) {
                new ReportMenu(player, this.player).open(player);
            }
        }
    }

    public String getStatusByPlayer(PotPlayer potPlayer) {
        if (potPlayer.isCurrentlyBlacklisted()) {
            return ChatColor.DARK_RED + "Blacklisted";
        }
        if (potPlayer.isCurrentlyRestricted()) {
            return ChatColor.RED + "Banned";
        }
        if (potPlayer.isCurrentlyMuted()) {
            return ChatColor.GOLD + "Muted";
        }

        return ChatColor.GREEN + "Online";
    }
}
