package com.solexgames.core.internal.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.internal.shared.TabElement;
import com.solexgames.core.internal.shared.TabElementHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TablistAdapter implements TabElementHandler {

    private Collection<Player> getOnlinePlayers(boolean filter) {
        if (filter) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> !CorePlugin.getInstance().getPlayerManager().getPlayer(player).isVanished())
                    .filter(player -> !CorePlugin.getInstance().getPlayerManager().getPlayer(player).isStaffMode())
                    .collect(Collectors.toList());
        }

        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    @Override
    public TabElement getElement(Player player) {
        final TabElement element = new TabElement();
        final AtomicInteger integer = new AtomicInteger();

        this.getOnlinePlayers(!player.hasPermission("scandium.staff")).stream()
                .map(player1 -> CorePlugin.getInstance().getPlayerManager().getPlayer(player1.getUniqueId()))
                .sorted(Comparator.comparingInt(potPlayer -> -potPlayer.getActiveGrant().getRank().getWeight()))
                .forEach(potPlayer -> element.add(integer.getAndIncrement(), potPlayer.getColorByRankColor() + potPlayer.getName()));

        return element;
    }
}
