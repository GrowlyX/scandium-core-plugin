package vip.potclub.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;

public class PunishSaveTask extends BukkitRunnable {

    public PunishSaveTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 36000L, 36000L);
    }

    @Override
    public void run() {
        Punishment.getAllPunishments().forEach(Punishment::savePunishment);
    }
}