package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.task.ShutdownTask;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@NoArgsConstructor
public class ShutdownManager {

    private ShutdownTask shutdownTask;

    private boolean shutdownScheduled;

    public void initiateShutdown(int seconds, Player initiator) {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = network.getMainColor();
        ChatColor secondaryColor = network.getSecondaryColor();

        Bukkit.broadcastMessage(secondaryColor + "The server will be shutting down in " + mainColor + seconds + " seconds" + secondaryColor + ".");
        CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&b[S] " + "&3[" + CorePlugin.getInstance().getServerName() + "] " + initiator.getDisplayName() + " &3has initiated a shutdown on &e" + CorePlugin.getInstance().getServerName() + "&3.");

        this.shutdownTask = new ShutdownTask(seconds);
        this.shutdownScheduled = true;
    }

    public void stopShutdown(Player stopper) {
        CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&b[S] " + "&3[" + CorePlugin.getInstance().getServerName() + "] " + stopper.getDisplayName() + " &3has stopped a shutdown on &e" + CorePlugin.getInstance().getServerName() + "&3.");
        Bukkit.broadcastMessage(Color.translate(ChatColor.RED + "The scheduled server shutdown has been cancelled."));

        this.shutdownTask.cancel();
        this.shutdownTask = null;
        this.shutdownScheduled = false;
    }
}
