package vip.potclub.core.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;

public class PlayerListener implements Listener {

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new PotPlayer(player.getUniqueId(), player.getAddress().getAddress());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PotPlayer potPlayer = CorePlugin.getInstance().playerManager.getPlayer(player.getUniqueId());
        potPlayer.savePlayerData();
    }
}
