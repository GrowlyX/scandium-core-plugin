package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.punishment.Punishment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class PunishExpireTask extends BukkitRunnable {

    public PunishExpireTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 30 * 20L);
    }

    @Override
    public void run() {
        Punishment.getAllPunishments()
                .stream()
                .filter(Objects::nonNull)
                .filter(Punishment::isActive)
                .filter(Punishment::checkIfActive)
                .forEach(punishment -> punishment.setActive(false));
    }
}
