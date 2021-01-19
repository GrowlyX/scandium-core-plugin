package vip.potclub.core.player.punishment;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum PunishmentType  {

    IPBAN("IPBan"),
    BAN("Ban"),
    BLACKLIST("Blacklist"),
    UNBLACKLIST("Unblacklist"),
    UNBAN("Unban"),
    MUTE("Mute"),
    UNMUTE("Unmute"),
    KICK("Kick"),
    WARN("Warn");

    private final String name;

    public String getName() {
        return this.name;
    }

    @ConstructorProperties({"name"})
    PunishmentType(String name) {
        this.name = name;
    }
}
