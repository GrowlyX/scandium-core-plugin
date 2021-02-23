package vip.potclub.core.task;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.util.Color;

@Getter
public class ShutdownTask extends BukkitRunnable {

    private int ticks;
    private final int seconds;

    public ShutdownTask(int seconds) {
        this.seconds = seconds;

        this.runTaskTimer(CorePlugin.getInstance(), this.seconds, 20L);
    }

    @Override
    public void run() {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = serverType.getMainColor();
        ChatColor secondaryColor = serverType.getSecondaryColor();

        int finalSeconds = seconds - ticks;

        switch (finalSeconds) {
            case 40:
            case 30:
            case 20:
            case 15:
            case 10:
                Bukkit.broadcastMessage(Color.translate(secondaryColor + "The server will be shutting down in " + mainColor + finalSeconds + " seconds" + secondaryColor + "."));
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