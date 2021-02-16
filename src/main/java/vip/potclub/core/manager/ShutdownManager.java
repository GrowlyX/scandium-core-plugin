package vip.potclub.core.manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.task.ShutdownTask;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

@NoArgsConstructor
@Getter
public class ShutdownManager {

    private ShutdownTask shutdownTask;

    private boolean shutdownScheduled;

    public void initiateShutdown(int seconds, Player initiator) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = serverType.getMainColor();
        ChatColor secondaryColor = serverType.getSecondaryColor();

        Bukkit.broadcastMessage(Color.translate(secondaryColor + "The server will be shutting down in " + mainColor + seconds + " seconds" + secondaryColor + "."));
        RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + initiator.getDisplayName() + " &bhas initiated a shutdown on &6" + CorePlugin.getInstance().getServerName() + "&b."), "scandium.staff"));

        this.shutdownTask = new ShutdownTask(seconds);
        this.shutdownScheduled = true;
    }

    public void stopShutdown(Player stopper) {
        RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + stopper.getDisplayName() + " &bhas stopped a shutdown on &6" + CorePlugin.getInstance().getServerName() + "&b."), "scandium.staff"));
        Bukkit.broadcastMessage(Color.translate(ChatColor.RED + "The scheduled server shutdown has been cancelled."));

        this.shutdownTask.cancel();
        this.shutdownTask = null;
        this.shutdownScheduled = false;
    }
}
