package vip.potclub.core.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;

public class FrozenMessageTask extends BukkitRunnable {

    public FrozenMessageTask() {
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 5 * 20L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> CorePlugin.getInstance().getPlayerManager().getPlayer(player).isFrozen())
                .forEach(player -> CorePlugin.getInstance().getPlayerManager().sendFreezeMessage(player));
    }
}
