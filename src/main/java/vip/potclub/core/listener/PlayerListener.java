package vip.potclub.core.listener;

import com.solexgames.perms.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.IMenu;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.RedisUtil;

import java.util.ArrayList;

public class PlayerListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof IMenu) {
            ((IMenu) event.getInventory().getHolder()).onInventoryClick(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof IMenu) {
            ((IMenu) event.getInventory().getHolder()).onInventoryDrag(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof IMenu) {
            ((IMenu) event.getInventory().getHolder()).onInventoryClose(event);
        }
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        // Loading in the Core Listener for the displayname setup.
        Profile profile = new Profile(event.getPlayer().getUniqueId(), new ArrayList<>(), new ArrayList<>());
        if (!profile.isLoaded()) {
            profile.asyncLoad();
        }
        profile.setupAtatchment();

        new PotPlayer(event.getPlayer().getUniqueId());
        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onConnect(event.getPlayer())));
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onDisconnect(event.getPlayer())));
        PotPlayer potPlayer = PotPlayer.getPlayer(event.getPlayer().getUniqueId());
        potPlayer.savePlayerData();

        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Profile.getProfiles().remove(profile);
        profile.save();
    }
}
