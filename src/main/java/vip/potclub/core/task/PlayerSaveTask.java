package vip.potclub.core.task;

import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;

public class PlayerSaveTask extends BukkitRunnable {

    public PlayerSaveTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 36000L, 36000L);
    }

    @Override
    public void run() {
        PotPlayer.getProfilePlayers().forEach((uuid, potPlayer) -> potPlayer.saveWithoutRemove());
    }
}
