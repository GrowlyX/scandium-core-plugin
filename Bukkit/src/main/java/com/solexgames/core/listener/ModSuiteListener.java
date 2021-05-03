package com.solexgames.core.listener;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.impl.player.ViewPlayerMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.impl.StaffViewPaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ModSuiteListener implements Listener {

    @EventHandler
    public void onEvent(BlockBreakEvent event) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (potPlayer.isStaffMode()) {
            event.setCancelled(true);
            potPlayer.getPlayer().sendMessage(Color.translate("&cYou cannot break blocks while in mod mode."));
        }
    }

    @EventHandler
    public void onEvent(BlockPlaceEvent event) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (potPlayer.isStaffMode()) {
            event.setCancelled(true);
            potPlayer.getPlayer().sendMessage(Color.translate("&cYou cannot place blocks while in mod mode."));
        }
    }

    @EventHandler
    public void onEvent(PlayerDropItemEvent event) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (potPlayer.isStaffMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEvent(PlayerInteractAtEntityEvent event) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (event.getRightClicked() instanceof Player) {
            final Player target = (Player) event.getRightClicked();

            if (target == null) {
                return;
            }

            final Player player = event.getPlayer();
            final ItemStack item = event.getPlayer().getItemInHand();

            if (potPlayer.isStaffMode()) {
                if (item != null) {
                    if (item.hasItemMeta()) {
                        final String materialName = item.getType().name().toLowerCase();

                        if (materialName.contains("packed")) {
                            final PotPlayer targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                            if (potPlayer.getActiveGrant().getRank() != null) {
                                if (potPlayer.getActiveGrant().getRank().getWeight() >= targetPotPlayer.getActiveGrant().getRank().getWeight() || player.isOp()) {
                                    potPlayer.getPlayer().chat("/freeze " + targetPotPlayer.getPlayer().getName());
                                } else {
                                    player.sendMessage(ChatColor.RED + ("You cannot freeze this player as their rank weight is higher than yours!"));
                                }
                            }
                        } else if (materialName.contains("book")) {
                            new ViewPlayerMenu(target).openMenu(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (potPlayer.isStaffMode()) {
                if (event.getItem() != null) {
                    if (event.getItem().hasItemMeta()) {
                        final String materialName = event.getItem().getType().name().toLowerCase();

                        if (materialName.contains("compass")) {
                            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(2.5F));
                        } else if (materialName.contains("skull")) {
                            new StaffViewPaginatedMenu(event.getPlayer()).openMenu(event.getPlayer());
                        } else if (materialName.contains("star")) {
                            Bukkit.getOnlinePlayers().stream()
                                    .filter(player -> !player.hasPermission("scandium.staff") && player != event.getPlayer())
                                    .findAny()
                                    .ifPresent(player -> {
                                        event.getPlayer().teleport(player.getLocation());
                                        event.getPlayer().sendMessage(ChatColor.GREEN + Color.translate("Teleported to &6" + player.getDisplayName() + ChatColor.GREEN + "!"));
                                    });
                        } else if (materialName.contains("ink")) {
                            event.getPlayer().performCommand("vanish");

                            ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
                            event.getPlayer().getInventory().setItem(8, new ItemBuilder((potPlayer.isVanished() ? XMaterial.LIME_DYE.parseMaterial() : XMaterial.LIGHT_GRAY_DYE.parseMaterial()), (potPlayer.isVanished() ? 10 : 8)).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + (potPlayer.isVanished() ? "Disable Vanish" : "Enable Vanish")).create());

                            event.getPlayer().updateInventory();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEvent(EntityDamageEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer((Player) event.getEntity());

            if (potPlayer != null) {
                if (potPlayer.isStaffMode()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
