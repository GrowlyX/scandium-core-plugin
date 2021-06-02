package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.task.ServerShutdownTask;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@NoArgsConstructor
public class ShutdownManager {

    private ServerShutdownTask serverShutdownTask;

    private boolean shutdownScheduled;

    public void initiateShutdown(int seconds, Player initiator) {
        Bukkit.broadcastMessage(Color.SECONDARY_COLOR + "The server will be shutting down in " + Color.MAIN_COLOR + seconds + " seconds" + Color.SECONDARY_COLOR + ".");
        CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&b[S] " + "&e[" + CorePlugin.getInstance().getServerName() + "] " + initiator.getDisplayName() + " &3has scheduled a shutdown on &e" + CorePlugin.getInstance().getServerName() + "&3.");

        this.serverShutdownTask = new ServerShutdownTask(seconds);
        this.shutdownScheduled = true;
    }

    public void stopShutdown(Player stopper) {
        CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&b[S] " + "&e[" + CorePlugin.getInstance().getServerName() + "] " + stopper.getDisplayName() + " &3has stopped a shutdown on &e" + CorePlugin.getInstance().getServerName() + "&3.");
        Bukkit.broadcastMessage(Color.translate(ChatColor.RED + "The scheduled server shutdown has been cancelled."));

        this.serverShutdownTask.cancel();
        this.serverShutdownTask = null;
        this.shutdownScheduled = false;
    }
}
