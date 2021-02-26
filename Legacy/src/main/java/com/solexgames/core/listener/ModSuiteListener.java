package com.solexgames.core.listener;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.extend.modsuite.FreezeCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.extend.modsuite.StaffMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class ModSuiteListener implements Listener {

    @EventHandler
    public void onEvent(BlockBreakEvent event) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (potPlayer.isStaffMode()) {
            event.setCancelled(true);
            potPlayer.getPlayer().sendMessage(Color.translate("&cYou cannot break blocks while in mod mode."));
        }
    }

    @EventHandler
    public void onEvent(BlockPlaceEvent event) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (potPlayer.isStaffMode()) {
            event.setCancelled(true);
            potPlayer.getPlayer().sendMessage(Color.translate("&cYou cannot place blocks while in mod mode."));
        }
    }

    @EventHandler
    public void onEvent(PlayerInteractAtEntityEvent event) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();
            Player player = event.getPlayer();
            ItemStack item = event.getPlayer().getItemInHand();

            if (potPlayer.isStaffMode()) {
                if (item != null) {
                    if (item.hasItemMeta()) {
                        switch (item.getType()) {
                            case PACKED_ICE:
                                PotPlayer targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                                if (potPlayer.getActiveGrant().getRank() != null) {
                                    if (potPlayer.getActiveGrant().getRank().getWeight() >= targetPotPlayer.getActiveGrant().getRank().getWeight()) {
                                        potPlayer.getPlayer().chat("/freeze " + targetPotPlayer.getPlayer().getName());
                                    } else {
                                        player.sendMessage(Color.translate("&cYou cannot freeze this player as their rank weight is higher than yours!"));
                                    }
                                }
                                break;
                            case BOOK:
                                player.sendMessage(Color.translate("&cNot finished!"));
                                break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (potPlayer.isStaffMode()) {
                if (event.getItem() != null) {
                    if (event.getItem().hasItemMeta()) {
                        switch (event.getMaterial()) {
                            case COMPASS:
                                event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(2.5F));
                                break;
                            case SKULL_ITEM:
                                new StaffMenu(event.getPlayer()).open(event.getPlayer());
                                break;
                            case NETHER_STAR:
                                Collections.singletonList(Bukkit.getOnlinePlayers()).get(CorePlugin.RANDOM.nextInt(Bukkit.getOnlinePlayers().size()))
                                        .stream()
                                        .findAny()
                                        .ifPresent(player -> {
                                    event.getPlayer().teleport(player.getLocation());
                                    event.getPlayer().sendMessage(Color.translate("&aTeleported to &b" + player.getDisplayName() + "&a!"));
                                });
                                break;
                            case INK_SACK:
                                event.getPlayer().performCommand("vanish");
                                ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
                                event.getPlayer().getInventory().setItem(8, new ItemBuilder(Material.INK_SACK, (potPlayer.isVanished() ? 10 : 8)).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + (potPlayer.isVanished() ? "Disable Vanish" : "Enable Vanish")).create());
                                break;
                        }
                        event.getPlayer().updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEvent(EntityDamageEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer((Player) event.getEntity());

            if (potPlayer.isStaffMode()) {
                event.setCancelled(true);
                potPlayer.getPlayer().sendMessage(Color.translate("&cYou cannot be damaged while in mod mode."));
            }
        }
    }
}