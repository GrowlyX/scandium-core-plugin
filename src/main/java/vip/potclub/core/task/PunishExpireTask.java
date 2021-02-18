package vip.potclub.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.punishment.Punishment;

import java.util.Date;
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
