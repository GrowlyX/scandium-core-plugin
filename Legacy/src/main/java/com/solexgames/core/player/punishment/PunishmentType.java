package com.solexgames.core.player.punishment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;

@Getter
@RequiredArgsConstructor
public enum PunishmentType  {

    IP_BAN("IP-Ban", "IP-Banned"),
    BAN("Ban", "Banned"),
    BLACKLIST("Blacklist", "Blacklisted"),
    MUTE("Mute", "Muted"),
    KICK("Kick", "Kicked"),
    WARN("Warn", "Warned");

    private final String name;
    private final String edName;

}
