package com.solexgames.core.internal.shared;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class TabRunnable extends BukkitRunnable {

    private final TabHandler handler;

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> handler.getAdapter()
                .showRealPlayers(player).addFakePlayers(player)
                .hideRealPlayers(player).handleElement(player, handler.getHandler().getElement(player)));
    }
}
