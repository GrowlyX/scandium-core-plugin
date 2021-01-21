package vip.potclub.core.player.punishment;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum PunishmentType  {

    IPBAN("IP-Ban", "IP-Banned"),
    BAN("Ban", "Banned"),
    BLACKLIST("Blacklist", "Blacklisted"),
    MUTE("Mute", "Muted"),
    KICK("Kick", "Kicked"),
    WARN("Warn", "Warned");

    private final String name;
    private final String edName;

    @ConstructorProperties({"name", "edName"})
    PunishmentType(String name, String edName) {
        this.name = name;
        this.edName = edName;
    }
}
