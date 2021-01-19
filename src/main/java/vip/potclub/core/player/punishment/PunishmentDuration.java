package vip.potclub.core.player.punishment;

import java.beans.ConstructorProperties;

public enum PunishmentDuration {

    SECOND(1000L, "s"),
    MINUTE(60L * SECOND.duration, "m"),
    HOUR(60L * MINUTE.duration, "h"),
    DAY(24L * HOUR.duration, "d"),
    WEEK(7L * DAY.duration, "w"),
    MONTH(30L * DAY.duration, "M"),
    YEAR(365L * DAY.duration, "y"),
    PERMANENT(Long.MAX_VALUE, "Permanent");

    private final long duration;
    private final String name;

    public static PunishmentDuration getByName(String name) {
        for (PunishmentDuration duration : values()) {
            if (duration.getName().equals(name)) {
                return duration;
            }

        }
        return null;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getName() {
        return this.name;
    }

    @ConstructorProperties({"duration", "name"})
    PunishmentDuration(long duration, String name) {
        this.duration = duration;
        this.name = name;
    }
}
