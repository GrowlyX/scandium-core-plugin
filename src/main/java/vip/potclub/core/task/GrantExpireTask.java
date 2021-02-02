package vip.potclub.core.task;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class GrantExpireTask extends BukkitRunnable {

    public GrantExpireTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        PotPlayer.getProfilePlayers().forEach((uuid, potPlayer) -> {
            potPlayer.getAllGrants().forEach(grant -> {
                if (!grant.isPermanent()) {
                    if (grant.isExpired() && grant.isActive()) {
                        grant.setActive(false);
                        potPlayer.setupAttachment();
                        if (potPlayer.getPlayer() != null) {
                            potPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Your rank has been set to " + Color.translate(potPlayer.getActiveGrant().getRank().getColor()) + potPlayer.getActiveGrant().getRank().getName() + ChatColor.GREEN + ".");
                        }
                    }
                }
            });
        });
    }
}
