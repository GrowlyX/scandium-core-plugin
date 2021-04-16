package com.solexgames.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
@AllArgsConstructor
public enum NetworkServerType {

    PRACTICE("Practice"),
    KITPVP("KitPvP"),
    HARDCORE_FACTIONS("HCF"),
    KITMAP("KitMap"),
    SKYWARS("SkyWars"),
    BEDWARS("BedWars"),
    MEETUP("UHC Meetup"),
    UHC_GAMES("UHC Games"),
    UHC("UHC"),
    HUB("Lobby"),
    EVENT("Event"),
    NOT_DEFINED("Booting..."),
    POTSG("PotSG");

    public final String serverTypeString;

}
