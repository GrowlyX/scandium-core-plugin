package vip.potclub.core.listener;

import com.solexgames.perms.profile.Profile;
import org.bukkit.ChatColor;
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
import vip.potclub.core.player.punishment.PunishmentStrings;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

import java.util.ArrayList;
import java.util.Date;

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
        Profile profile = new Profile(event.getPlayer().getUniqueId(), new ArrayList<>(), new ArrayList<>());
        if (!profile.isLoaded()) {
            profile.asyncLoad();
        }
        profile.setupAtatchment();

        PotPlayer potPlayer = new PotPlayer(event.getPlayer().getUniqueId());
        potPlayer.getPunishments().forEach(punishment -> {
            if ((punishment.getPunishmentType().equals(PunishmentType.BAN)) || (punishment.getPunishmentType().equals(PunishmentType.BLACKLIST)) || (punishment.getPunishmentType().equals(PunishmentType.IPBAN))) {
                if (punishment.isActive() || !punishment.isRemoved()) {
                    if (punishment.getTarget().equals(event.getPlayer().getUniqueId())) {
                        switch (punishment.getPunishmentType()) {
                            case BLACKLIST:
                                event.getPlayer().kickPlayer(Color.translate(PunishmentStrings.BLCK_MESSAGE.replace("<reason>", punishment.getReason())));
                                break;
                            case BAN:
                                event.getPlayer().kickPlayer((punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
                                break;
                        }
                    }
                }
            }
        });

        if (event.getPlayer().hasPermission("core.staff")) {
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onConnect(event.getPlayer())));
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("core.staff")) {
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onDisconnect(event.getPlayer())));
        }
        PotPlayer potPlayer = PotPlayer.getPlayer(event.getPlayer().getUniqueId());
        potPlayer.savePlayerData();

        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Profile.getProfiles().remove(profile);
        profile.save();
    }
}
