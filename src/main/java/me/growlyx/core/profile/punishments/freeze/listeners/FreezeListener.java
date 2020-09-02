package me.growlyx.core.profile.punishments.freeze.listeners;

import me.growlyx.core.profile.punishments.freeze.handlers.ListenerHandler;
import me.growlyx.core.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FreezeListener implements Listener {
    private ListenerHandler handler;

    public FreezeListener(final ListenerHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        if (this.handler.getPlugin().getManagerHandler().getFrozenManager().isFrozen(player.getUniqueId())) {
            this.handler.getPlugin().getServer().broadcast(CC.translate("&7[&4&l!&7] &4" + player.getDisplayName() + "&7left while frozen!"), "core.staff");
            this.handler.getPlugin().getManagerHandler().getFrozenManager().unfreezeUUID(player.getUniqueId());
            this.handler.getPlugin().getManagerHandler().getPlayerSnapshotManager().restorePlayer(player);
        }
    }

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent e) {
        if (this.handler.getPlugin().getManagerHandler().getFrozenManager().isFrozen(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvClose(final InventoryCloseEvent e) {
        final Player player = (Player) e.getPlayer();
        if (this.handler.getPlugin().getManagerHandler().getFrozenManager().isFrozen(player.getUniqueId())) {
            new BukkitRunnable() {
                public void run() {
                    player.openInventory(handler.getPlugin().getManagerHandler().getInventoryManager().getFrozenInv());
                }
            }.runTaskLater((Plugin)this.handler.getPlugin(), 1L);
        }
    }

    @EventHandler
    public void onInvClick(final InventoryClickEvent e) {
        final Player player = (Player)e.getWhoClicked();
        if (this.handler.getPlugin().getManagerHandler().getFrozenManager().isFrozen(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player player = (Player) e.getEntity();
            if (this.handler.getPlugin().getManagerHandler().getFrozenManager().isFrozen(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            final Player player = (Player) e.getDamager();
            if (this.handler.getPlugin().getManagerHandler().getFrozenManager().isFrozen(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

}
