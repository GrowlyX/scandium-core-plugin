package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.task.ShutdownTask;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + initiator.getDisplayName() + " &bhas initiated a shutdown on &6" + CorePlugin.getInstance().getServerName() + "&b.");

        this.shutdownTask = new ShutdownTask(seconds);
        this.shutdownScheduled = true;
    }

    public void stopShutdown(Player stopper) {
        CorePlugin.getInstance().getPlayerManager().sendToNetworkStaff("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + stopper.getDisplayName() + " &bhas stopped a shutdown on &6" + CorePlugin.getInstance().getServerName() + "&b.");
        Bukkit.broadcastMessage(Color.translate(ChatColor.RED + "The scheduled server shutdown has been cancelled."));

        this.shutdownTask.cancel();
        this.shutdownTask = null;
        this.shutdownScheduled = false;
    }
}
