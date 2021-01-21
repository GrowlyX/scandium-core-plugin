package vip.potclub.core.player.punishment;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum PunishmentType  {

    IPBAN("IPBan"),
    BAN("Ban"),
    BLACKLIST("Blacklist"),
    MUTE("Mute"),
    KICK("Kick"),
    WARN("Warn");

    private final String name;

    @ConstructorProperties({"name"})
    PunishmentType(String name) {
        this.name = name;
    }
}
