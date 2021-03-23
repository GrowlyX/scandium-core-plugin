package com.solexgames.core.abstraction.tablist;

import cc.outlast.tablist.ITablist;
import cc.outlast.tablist.TablistElement;
import com.solexgames.core.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TablistAdapter implements ITablist {

    @Override
    public List<TablistElement> getElements(Player player) {
        List<TablistElement> elements = new ArrayList<>();
        AtomicInteger integer = new AtomicInteger();

        this.getOnlinePlayers(!player.hasPermission("scandium.staff")).stream()
                .map(player1 -> CorePlugin.getInstance().getPlayerManager().getPlayer(player1.getUniqueId()))
                .sorted(Comparator.comparingInt(potPlayer -> -potPlayer.getActiveGrant().getRank().getWeight()))
                .forEach(potPlayer -> elements.add(
                        new TablistElement(potPlayer.getColorByRankColor() + potPlayer.getName(), integer.getAndIncrement())
                ));

        return elements;
    }

    @Override
    public String getHeader(Player player) {
        return "";
    }

    @Override
    public String getFooter(Player player) {
        return "";
    }

    private Collection<Player> getOnlinePlayers(boolean filter) {
        if (!filter) {
            return new ArrayList<>(Bukkit.getOnlinePlayers());
        } else {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> !CorePlugin.getInstance().getPlayerManager().getPlayer(player).isVanished())
                    .filter(player -> !CorePlugin.getInstance().getPlayerManager().getPlayer(player).isStaffMode())
                    .collect(Collectors.toList());
        }
    }
}
