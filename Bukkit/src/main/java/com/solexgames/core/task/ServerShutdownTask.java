package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author GrowlyX
 * @since March 2021
 */

@Getter
public class ServerShutdownTask extends BukkitRunnable {

    private int ticks;
    private final int seconds;

    public ServerShutdownTask(int seconds) {
        this.seconds = seconds;

        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        final int finalSeconds = this.seconds - this.ticks;

        switch (finalSeconds) {
            case 40: case 30: case 20: case 15:
            case 10:
                Bukkit.broadcastMessage(Color.SECONDARY_COLOR + "The server will be shutting down in " + Color.MAIN_COLOR + finalSeconds + " seconds" + Color.SECONDARY_COLOR + ".");
                break;
            case 5:
                Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(Color.translate("&cThe server is currently rebooting...\n&cPlease reconnect in a few minutes.")));
                CorePlugin.getInstance().getServer().shutdown();
            default:
                break;
        }

        ticks++;
    }
}
