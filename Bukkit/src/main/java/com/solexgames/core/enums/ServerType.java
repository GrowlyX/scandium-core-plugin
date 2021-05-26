package com.solexgames.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@Getter
@RequiredArgsConstructor
public enum ServerType {

    BLARE("Blare", "01", ChatColor.AQUA, ChatColor.DARK_AQUA, "https://dsc.gg/blare", "BlareNetworkRIP", "shop.blare.rip", "blare.rip"),
    SCANDIUM("SolexGames", "04", ChatColor.GOLD, ChatColor.YELLOW, "https://solexgames.com", "SolexDev", "store.solexgames.com", "demo.solexgames.com"),
    METEORITE("Meteorite", "05", ChatColor.DARK_PURPLE, ChatColor.AQUA, "https://discord.com/invite/zydbyNBbX5", "Soon!", "store.meteoritemc.net", "https://discord.com/invite/zydbyNBbX5"),
    ADVENTURE("Adventure", "08", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, "https://discord.adventure.rip/", "AdventureRIP", " store.adventure.rip", "adventure.rip"),
    TERRORPVP("Terror PvP", "09", ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, "https://discord.terrorpvp.rip", "TerrorPvPRip", "store.terrorpvp.rip", "terrorpvp.rip"),
    MYTHIA("Mythia", "10", ChatColor.DARK_BLUE, ChatColor.GRAY, "https://discord.gg/ykTBmG9KRj", "MythiaMC", "store.mythia.us", "mythia.us"),
    VERIS("VerisPvP", "12", ChatColor.DARK_PURPLE, ChatColor.YELLOW, "https://discord.gg/6p4pNbTDxc", "VerisClub", "store.veris.club", "veris.club"),
    MINEARCADE("MineArcade", "13", ChatColor.DARK_AQUA, ChatColor.YELLOW, "https://dsc.gg/minearcade", "MineArcadeORG", "shop.minearcade.org", "play.minearcade.org"),
    DEVOUT("DevoutPvP", "14", ChatColor.DARK_RED, ChatColor.RED, "discord.gg/devout", "DevoutPvP", "store.devoutpvp.com", "devoutpvp.com"),
    POGPUBLIC("PogPublic", "15", ChatColor.AQUA, ChatColor.GRAY, "https://discord.gg/U4EZ9eh9xd", "Pog_Public", "https://store.pogpublic.com/", "fun.pogpublic.com"),
    APPOLIX("AppolixMC", "16", ChatColor.GOLD, ChatColor.YELLOW, "https://discord.gg/mtrWYcAp7c", "AppolixMC", "https://store.appolixmc.com/", "appolixmc.com"),
    KYRO("Kryo Network", "17", ChatColor.BLUE, ChatColor.WHITE, "https://discord.kyro.rip", "KyroNetwork", "https://store.kyro.rip/", "kyro.rip"),
    PVPBAR("PvPBar", "18", ChatColor.GOLD, ChatColor.YELLOW, "https://pvp.bar/discord", "PvPBarMC", "https://store.pvp.bar", "pvp.bar"),
    MAGE("Mage Network", "19", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, "https://discord.mage.rip", "", "https://store.mage.rip/", "mage.rip"),
    FROSTPVP("FrostPvP", "20", ChatColor.AQUA, ChatColor.WHITE, "https://frostpvp.net/discord", "FrostPvPs", "https://store.frostpvp.net/", "frostpvp.net"),
    AURAPVP("AuraPvP", "21", ChatColor.DARK_RED, ChatColor.GRAY, "https://discord.aurapvp.com", "AuraPvPCOM", "https://store.aurapvp.com/", "aurapvp.com"),
    CURSEDMC("CursedMC", "22", ChatColor.DARK_RED, ChatColor.RED, "https://discord.gg/TVcHvykG", "CursedMCRip", "https://store.cursedmc.rip/", "cursedmc.rip"),
    TWISTMC("TwistMC", "23", ChatColor.LIGHT_PURPLE, ChatColor.GREEN, "https://discord.gg/6E9q4VgKPz", "TwistBedWars", "https://store.twistmc.net/", "play.twistmc.net"),
    INVISPVP("InvisPvP", "24", ChatColor.GOLD, ChatColor.GRAY, "https://discord.invispvp.com", "InvisPvP", "https://store.invispvp.com/", "invispvp.com");

    private final String serverName;
    private final String serverId;

    private final ChatColor mainColor;
    private final ChatColor secondaryColor;

    private final String discordLink;
    private final String twitterLink;
    private final String storeLink;
    private final String websiteLink;

}
