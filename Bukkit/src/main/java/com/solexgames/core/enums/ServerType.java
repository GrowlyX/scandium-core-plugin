package com.solexgames.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

import java.beans.ConstructorProperties;

/**
 * Stores all servers which Scandium is used on.
 */

@Getter
@RequiredArgsConstructor
public enum ServerType {

    BLARE("Blare Network", "01", ChatColor.AQUA, ChatColor.DARK_AQUA, "https://dsc.gg/blare", "BlareNetworkRIP", "shop.blare.rip", "blare.rip"),
    EVENTIS("Eventis Network", "03", ChatColor.AQUA, ChatColor.DARK_AQUA, "https://discord.eventis.club", "EventisClubMC", "store.eventis.club", "eventis.club"),
    SCANDIUM("SolexGames", "04", ChatColor.GOLD, ChatColor.YELLOW, "https://solexgames.com", "SolexDev", "store.solexgames.com", "demo.solexgames.com"),
    METEORITE("Meteorite Network", "05", ChatColor.DARK_PURPLE, ChatColor.AQUA, "https://discord.com/invite/zydbyNBbX5", "Soon!", "store.meteoritemc.net", "https://discord.com/invite/zydbyNBbX5"),
    ADVENTURE("Adventure Network", "08", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, "https://discord.adventure.rip/", "AdventureRIP", " store.adventure.rip", "adventure.rip"),
    TERRORPVP("Terror PvP", "09", ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, "https://discord.terrorpvp.rip", "TerrorPvPRip", "store.terrorpvp.rip", "terrorpvp.rip"),
    MYTHIA("Mythia Network", "10", ChatColor.DARK_BLUE, ChatColor.GRAY, "https://discord.gg/ykTBmG9KRj", "MythiaMC", "store.mythia.us", "mythia.us"),
    MCL("MCLeague", "11", ChatColor.DARK_AQUA, ChatColor.YELLOW, "https://discord.gg/75x7VpV8EX", "MCLeagueEvents", "store.mcleague.net", "mcleague.net"),
    VERIS("Veris Network", "12", ChatColor.DARK_PURPLE, ChatColor.YELLOW, "https://discord.gg/6p4pNbTDxc", "VerisClub", "store.veris.club", "veris.club"),
    MINEARCADE("MineArcade", "13", ChatColor.DARK_AQUA, ChatColor.YELLOW, "https://dsc.gg/minearcade", "MineArcadeORG", "shop.minearcade.org", "play.minearcade.org"),
    DEVOUT("DevoutPvP", "14", ChatColor.DARK_RED, ChatColor.RED, "discord.gg/devout", "DevoutPvP", "store.devoutpvp.com", "devoutpvp.com"),
    POGPUBLIC("PogPublic", "15", ChatColor.AQUA, ChatColor.GRAY, "https://discord.gg/U4EZ9eh9xd", "Pog_Public", "https://store.pogpublic.com/", "fun.pogpublic.com"),
    ARLO("Arlo Network", "16", ChatColor.DARK_GREEN, ChatColor.GRAY, "https://discord.arlo.rip", "ArloNetworkMC", "https://store.arlo.rip/", "arlo.rip");

    private final String serverName;
    private final String serverId;

    private final ChatColor mainColor;
    private final ChatColor secondaryColor;

    private final String discordLink;
    private final String storeLink;
    private final String twitterLink;
    private final String websiteLink;

}