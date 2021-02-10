package vip.potclub.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.punishment.Punishment;

import java.util.Date;

public class PunishExpireTask extends BukkitRunnable {

    public PunishExpireTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 30 * 20L);
    }

    @Override
    public void run() {
        Punishment.getAllPunishments().forEach(punishment -> {
            if (punishment.isActive()) {
                if (!punishment.isRemoved()) {
                    if (!punishment.isPermanent()) {
                        if (new Date(punishment.getCreatedAt().getTime() + punishment.getPunishmentDuration()).compareTo(new Date()) >= 0) {
                            punishment.setActive(false);
                        }
                    }
                }
            }
        });
    }
}
