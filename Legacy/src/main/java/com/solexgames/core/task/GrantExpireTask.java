package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class GrantExpireTask extends BukkitRunnable {

    public GrantExpireTask() {
        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        CorePlugin.getInstance().getPlayerManager().getAllProfiles().forEach((uuid, potPlayer) -> potPlayer.getAllGrants().stream()
                .filter(grant -> grant != null && grant.isExpired() && grant.isActive() && !grant.isPermanent())
                .forEach(grant -> {
                    grant.setActive(false);
                    potPlayer.setupPlayer();

                    if (potPlayer.getPlayer() != null)
                        potPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Your rank has been set to " + Color.translate(potPlayer.getActiveGrant().getRank().getColor()) + potPlayer.getActiveGrant().getRank().getName() + ChatColor.GREEN + ".");
                }));
    }
}
