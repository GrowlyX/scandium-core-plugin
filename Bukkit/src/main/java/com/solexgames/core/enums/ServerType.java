package com.solexgames.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@Getter
@RequiredArgsConstructor
public enum ServerType {

    SCANDIUM("SolexGames", ChatColor.GOLD, ChatColor.YELLOW, "https://solexgames.com", "SolexDev", "store.solexgames.com", "demo.solexgames.com"),
    METEORITE("Meteorite", ChatColor.DARK_PURPLE, ChatColor.AQUA, "https://discord.com/invite/zydbyNBbX5", "Soon!", "store.meteoritemc.net", "https://discord.com/invite/zydbyNBbX5"),
    ADVENTURE("Adventure", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, "https://discord.adventure.rip/", "AdventureRIP", " store.adventure.rip", "adventure.rip"),
    VERIS("VerisPvP", ChatColor.DARK_PURPLE, ChatColor.YELLOW, "https://discord.gg/6p4pNbTDxc", "VerisClub", "store.veris.club", "veris.club"),
    DEVOUT("DevoutPvP", ChatColor.DARK_RED, ChatColor.RED, "discord.gg/devout", "DevoutPvP", "store.devoutpvp.com", "devoutpvp.com"),
    POGPUBLIC("PogPublic", ChatColor.AQUA, ChatColor.GRAY, "https://discord.gg/U4EZ9eh9xd", "Pog_Public", "https://store.pogpublic.com/", "fun.pogpublic.com"),
    APPOLIX("AppolixMC", ChatColor.GOLD, ChatColor.YELLOW, "https://discord.gg/mtrWYcAp7c", "AppolixMC", "https://store.appolixmc.com/", "appolixmc.com"),
    KYRO("Kryo Network", ChatColor.BLUE, ChatColor.WHITE, "https://discord.kyro.rip", "KyroNetwork", "https://store.kyro.rip/", "kyro.rip"),
    PVPBAR("PvPBar", ChatColor.GOLD, ChatColor.YELLOW, "https://pvp.bar/discord", "PvPBarMC", "https://store.pvp.bar", "pvp.bar"),
    FROSTPVP("FrostPvP", ChatColor.AQUA, ChatColor.WHITE, "https://frostpvp.net/discord", "FrostPvPs", "https://store.frostpvp.net/", "frostpvp.net"),
    CURSEDMC("CursedMC", ChatColor.DARK_RED, ChatColor.RED, "https://discord.gg/TVcHvykG", "CursedMCRip", "https://store.cursedmc.rip/", "cursedmc.rip"),
    TWISTMC("TwistMC", ChatColor.LIGHT_PURPLE, ChatColor.GREEN, "https://discord.gg/6E9q4VgKPz", "TwistBedWars", "https://store.twistmc.net/", "play.twistmc.net"),
    INVISPVP("InvisPvP", ChatColor.GOLD, ChatColor.GRAY, "https://discord.invispvp.com", "InvisPvP", "https://store.invispvp.com/", "invispvp.com"),
    KRYPTIK("Kryptik", ChatColor.DARK_PURPLE, ChatColor.WHITE, "https://discord.gg/Q55aKR4kyZ", "KryptikNetwork", "https://store.kryptikpvp.com", "kryptikpvp.com"),
    FAKEMEN("FakemenClub", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, "https://discord.gg/fakemen", "FakemenNetwork", "https://store.fakemen.club/", "fakemen.club"),
    ;

    private final String serverName;

    private final ChatColor mainColor;
    private final ChatColor secondaryColor;

    private final String discordLink;
    private final String twitterLink;
    private final String storeLink;
    private final String websiteLink;

}
