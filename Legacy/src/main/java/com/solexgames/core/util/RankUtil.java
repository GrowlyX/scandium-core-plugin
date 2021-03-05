package com.solexgames.core.util;

import com.solexgames.core.player.ranks.Rank;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

public final class RankUtil {

    private static final List<Rank> RANKS = Rank.getRanks().stream()
            .filter(rank -> !rank.isHidden())
            .sorted(Comparator.comparingInt(rank -> -rank.getWeight()))
            .collect(Collectors.toList());

    public static int getPositionByRank(Rank rank) {
        return RankUtil.RANKS.indexOf(rank);
    }
}
