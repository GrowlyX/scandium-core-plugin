package com.solexgames.core.player.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;

/**
 * @author GrowlyX
 * @since March 2021
 */

@Getter
@RequiredArgsConstructor
public enum PunishmentDuration {

    SECOND(1000L, "s"),
    MINUTE(60L * SECOND.getDuration(), "m"),
    HOUR(60L * MINUTE.getDuration(), "h"),
    DAY(24L * HOUR.getDuration(), "d"),
    WEEK(7L * DAY.getDuration(), "w"),
    MONTH(30L * DAY.getDuration(), "M"),
    YEAR(365L * DAY.getDuration(), "y"),
    PERMANENT(Long.MAX_VALUE * YEAR.getDuration(), "Permanent");

    private final long duration;
    private final String name;

}
