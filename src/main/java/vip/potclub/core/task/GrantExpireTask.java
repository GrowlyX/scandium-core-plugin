package vip.potclub.core.task;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.grant.Grant;
import vip.potclub.core.util.Color;

import java.util.Objects;

public class GrantExpireTask extends BukkitRunnable {

    public GrantExpireTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        PotPlayer.getProfilePlayers().forEach((uuid, potPlayer) -> potPlayer.getAllGrants()
                .stream()
                .filter(Objects::nonNull)
                .filter(Grant::isExpired)
                .filter(Grant::isActive)
                .filter(grant -> !grant.isPermanent())
                .forEach(grant -> {
                    grant.setActive(false);
                    potPlayer.setupAttachment();

                    if (potPlayer.getPlayer() != null) potPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Your rank has been set to " + Color.translate(potPlayer.getActiveGrant().getRank().getColor()) + potPlayer.getActiveGrant().getRank().getName() + ChatColor.GREEN + ".");
                }));
    }
}
