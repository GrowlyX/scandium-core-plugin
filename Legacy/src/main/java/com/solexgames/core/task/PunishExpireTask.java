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

public class PunishExpireTask extends BukkitRunnable {

    public PunishExpireTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 10 * 20L);
    }

    @Override
    public void run() {
        Punishment.getAllPunishments()
                .stream()
                .filter(Objects::nonNull)
                .filter(Punishment::isActive)
                .filter(Punishment::checkIfActive)
                .forEach(punishment -> {
                    punishment.setActive(false);

                    Player player = Bukkit.getPlayer(punishment.getTarget());

                    if (player != null) {
                        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                        switch (punishment.getPunishmentType()) {
                            case MUTE:
                                potPlayer.setCurrentlyMuted(false);
                                break;
                            case WARN:
                                player.sendMessage(ChatColor.RED + "Your current warning has now expired.");
                                break;
                            case BLACKLIST:
                            case IPBAN:
                            case BAN:
                                potPlayer.setCurrentlyRestricted(false);
                                break;
                        }
                    }
                });
    }
}
