package vip.potclub.core.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class PlayerListener implements Listener {

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        new PotPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        PotPlayer potPlayer = PotPlayer.getPlayer(event.getPlayer().getUniqueId());
        potPlayer.savePlayerData();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        PotPlayer potPlayer = PotPlayer.getPlayer(event.getPlayer().getUniqueId());
        String color = potPlayer.getRank().getColor();
        Bukkit.broadcastMessage(Color.translate(potPlayer.getRank().getPrefix() + color + event.getPlayer().getName() + ChatColor.WHITE + ": ") + event.getMessage());
    }
}
