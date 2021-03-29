package com.solexgames.core.player.punishment;

import lombok.Getter;

import java.beans.ConstructorProperties;

/**
 * @author GrowlyX
 * @since March 2021
 */

@Getter
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

    @ConstructorProperties({"duration", "name"})
    PunishmentDuration(long duration, String name) {
        this.duration = duration;
        this.name = name;
    }
}
