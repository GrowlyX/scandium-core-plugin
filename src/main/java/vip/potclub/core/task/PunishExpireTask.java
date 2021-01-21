package vip.potclub.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.punishment.Punishment;

import java.util.Date;

public class PunishExpireTask extends BukkitRunnable {

    public PunishExpireTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 120 * 20L);
    }

    @Override
    public void run() {
        Punishment.getAllPunishments().forEach(punishment -> {
            if (punishment.isActive() || !punishment.isPermanent() || !punishment.isRemoved()) {
                if (new Date(punishment.getCreatedAt() + punishment.getPunishmentDuration()).compareTo(new Date()) >= 0) {
                    punishment.setActive(false);
                }
            }
        });
    }
}
