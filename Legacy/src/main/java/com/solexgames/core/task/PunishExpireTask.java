package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class PunishExpireTask extends BukkitRunnable {

    public PunishExpireTask() {
        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        CompletableFuture.runAsync(() -> Punishment.getAllPunishments().stream()
                .filter(punishment -> punishment != null && !punishment.isRemoved() && punishment.isActive() && (System.currentTimeMillis() <= punishment.getCreatedAt().getTime() + punishment.getPunishmentDuration()) && !punishment.isPermanent())
                .forEach(punishment -> {
                    punishment.setActive(false);

                    Player player = Bukkit.getPlayer(punishment.getTarget());

                    if (player != null) {
                        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                        switch (punishment.getPunishmentType()) {
                            case MUTE:
                                potPlayer.setCurrentlyMuted(false);
                                player.sendMessage(ChatColor.RED + "Your current mute has expired.");
                                break;
                            case WARN:
                                potPlayer.setHasActiveWarning(false);
                                potPlayer.setWarningPunishment(null);
                                player.sendMessage(ChatColor.RED + "Your current warning has expired.");
                                break;
                            case BLACKLIST:
                            case IPBAN:
                            case BAN:
                                potPlayer.setCurrentlyRestricted(false);
                                player.sendMessage(ChatColor.RED + "Your current ban has expired.");
                                break;
                        }
                    }
                }));
    }
}
