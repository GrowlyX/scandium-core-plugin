package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

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
                    potPlayer.setupPlayer();

                    if (potPlayer.getPlayer() != null) potPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Your rank has been set to " + Color.translate(potPlayer.getActiveGrant().getRank().getColor()) + potPlayer.getActiveGrant().getRank().getName() + ChatColor.GREEN + ".");
                }));
    }
}