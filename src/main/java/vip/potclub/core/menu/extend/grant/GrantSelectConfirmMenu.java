package vip.potclub.core.menu.extend.grant;

import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.grant.Grant;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;

import java.util.Arrays;

@Getter
public class GrantSelectConfirmMenu extends AbstractInventoryMenu<CorePlugin> {

    private final Player player;
    private final Player target;

    private final Rank rank;
    private final String reason;

    private final long duration;
    private final boolean permanent;

    public GrantSelectConfirmMenu(Player player, Player target, Rank rank, long duration, String reason, boolean permanent) {
        super("Grants - Confirm", 9*3);
        this.player = player;
        this.target = target;
        this.rank = rank;
        this.duration = duration;
        this.reason = reason;
        this.permanent = permanent;

        this.update();
    }

    private void update() {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        this.inventory.setItem(13, new InventoryMenuItem(Material.NETHER_STAR).setDisplayName("&bConfirm Grant").addLore(Arrays.asList(
                "",
                network.getSecondaryColor() + "Issuer: " + network.getMainColor() + player.getDisplayName(),
                network.getSecondaryColor() + "Target: " + network.getMainColor() + target.getDisplayName(),
                network.getSecondaryColor() + "Rank: " + network.getMainColor() + rank.getColor() + rank.getName(),
                network.getSecondaryColor() + "Duration: " + network.getMainColor()  + (isPermanent() ? "&4Forever" : DurationFormatUtils.formatDurationWords(duration, true, true)),
                network.getSecondaryColor() + "Reason: " + network.getMainColor()  + reason,
                "",
                "&bClick to confirm grant."
        )).create());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            if (event.getSlot() == 13) {
                Grant newGrant = new Grant(player.getUniqueId(), rank, System.currentTimeMillis(), System.currentTimeMillis() - duration, reason, true, permanent);
                PotPlayer targetPotPlayer = PotPlayer.getPlayer(target);

                targetPotPlayer.getAllGrants().add(newGrant);
                targetPotPlayer.setupAttachment();
                targetPotPlayer.saveWithoutRemove();

                target.sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                player.sendMessage(ChatColor.GREEN + Color.translate("Set " + target.getDisplayName() + ChatColor.GREEN + "'s rank to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));

                player.closeInventory();
            }
        }
    }
}
