package vip.potclub.core.modsuite;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

import java.util.Collections;

public class ModSuiteListener implements Listener {

    @EventHandler
    public void onEvent(BlockBreakEvent event) {
        PotPlayer potPlayer = PotPlayer.getPlayer(event.getPlayer());

        if (potPlayer.isStaffMode()) {
            event.setCancelled(true);
            potPlayer.getPlayer().sendMessage(Color.translate("&cYou cannot break blocks while in mod mode."));
        }
    }

    @EventHandler
    public void onEvent(BlockPlaceEvent event) {
        PotPlayer potPlayer = PotPlayer.getPlayer(event.getPlayer());

        if (potPlayer.isStaffMode()) {
            event.setCancelled(true);
            potPlayer.getPlayer().sendMessage(Color.translate("&cYou cannot place blocks while in mod mode."));
        }
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        PotPlayer potPlayer = PotPlayer.getPlayer(event.getPlayer());

        if (potPlayer.isStaffMode()) {
            if (event.getItem().hasItemMeta()) {
                switch (event.getMaterial()) {
                    case COMPASS:
                        event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(2.5F));
                        break;
                    case SKULL_ITEM:
                        event.getPlayer().sendMessage(Color.translate("&aIn development."));
                        break;
                    case NETHER_STAR:
                        Collections.singletonList(Bukkit.getOnlinePlayers()).get(CorePlugin.RANDOM.nextInt(Bukkit.getOnlinePlayers().size())).stream().findFirst().ifPresent(player -> {
                            event.getPlayer().teleport(player.getLocation());
                            event.getPlayer().sendMessage(Color.translate("&aTeleported to &b" + player.getDisplayName() + "&a!"));
                        });
                        break;
                    case BOOK:
                        event.getPlayer().sendMessage(Color.translate("&aIn development."));
                        break;
                    case PACKED_ICE:
                        event.getPlayer().sendMessage(Color.translate("&aIn development."));
                        break;
                    case INK_SACK:
                        event.getPlayer().performCommand("vanish");
                        break;
                }
                event.getPlayer().updateInventory();
            }
        }
    }

    @EventHandler
    public void onEvent(EntityDamageEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            PotPlayer potPlayer = PotPlayer.getPlayer((Player) event.getEntity());

            if (potPlayer.isStaffMode()) {
                event.setCancelled(true);
                potPlayer.getPlayer().sendMessage(Color.translate("&cYou cannot be damaged while in mod mode."));
            }
        }
    }
}
