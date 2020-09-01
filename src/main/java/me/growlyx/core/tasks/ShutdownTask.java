package me.growlyx.core.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ShutdownTask extends BukkitRunnable
{
    private final List<Integer> BROADCAST_TIMES;
    private Core plugin;
    private int secondsUntilShutdown;

    public void run() {
        if (this.BROADCAST_TIMES.contains(this.secondsUntilShutdown)) {
            this.plugin.getServer().broadcastMessage(CC.translate(""));
        }
        if (this.secondsUntilShutdown <= 0) {
            this.plugin.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(CC.translate("&cThe server has shut down.")));
            this.plugin.getServer().shutdown();
        }
        --this.secondsUntilShutdown;
    }

    public List<Integer> getBROADCAST_TIMES() {
        return this.BROADCAST_TIMES;
    }

    public Core getPlugin() {
        return this.plugin;
    }

    public int getSecondsUntilShutdown() {
        return this.secondsUntilShutdown;
    }

    public void setPlugin(final Core plugin) {
        this.plugin = plugin;
    }

    public void setSecondsUntilShutdown(final int secondsUntilShutdown) {
        this.secondsUntilShutdown = secondsUntilShutdown;
    }

    public ShutdownTask(final Core plugin, final int secondsUntilShutdown) {
        this.BROADCAST_TIMES = Arrays.asList(3600, 1800, 900, 600, 300, 180, 120, 60, 45, 30, 15, 10, 5, 4, 3, 2, 1);
        this.plugin = plugin;
        this.secondsUntilShutdown = secondsUntilShutdown;
    }
}
