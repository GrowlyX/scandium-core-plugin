package com.solexgames.core.enums;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.beans.ConstructorProperties;

@Getter
public enum ServerType {

    POTCLUBVIP("PotClub Network", "01", ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, "https://dsc.gg/pot", "PotClubVIP", "store.potclub.vip", "eu.potclub.vip"),
    EVENTIS("Eventis Network", "03", ChatColor.AQUA, ChatColor.DARK_AQUA, "https://discord.eventis.club", "EventisClubMC", "store.eventis.club", "eventis.club"),
    SCANDIUM("Scandium Network", "04", ChatColor.GOLD, ChatColor.YELLOW, "https://solexgames.com", "SolexDev", "store.solexgames.com", "solexgames.com"),
    BUZZMC("BuzzMC Network", "05", ChatColor.RED, ChatColor.AQUA, "https://discord.gg/683arg8CZE", "Coming Soon", "store.buzzmc.cf", "https://discord.gg/683arg8CZE"),
    ICE("Ice Network", "06", ChatColor.AQUA, ChatColor.WHITE, "https://discord.ice.rip", "IceNetworkRIP", "store.ice.rip", "ice.rip"),
    EYTRILORG("Eytril Network", "07", ChatColor.GOLD, ChatColor.WHITE, "https://discord.eytril.org/", "EytrilOrg", "store.eytril.org", "eytril.org"),
    ADVENTURE("Adventure Network", "08", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, "https://discord.adventure.rip/", "AdventureRIP", " store.adventure.rip", "adventure.rip"),
    TERRORPVP("Terror PvP", "09", ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, "https://discord.terrorpvp.rip", "TerrorPvPNetwork", " store.terrorpvp.rip", "terrorpvp.rip"),
    MYTHIA("Mythia Network", "10", ChatColor.DARK_BLUE, ChatColor.GRAY, "https://discord.gg/ykTBmG9KRj", "MythiaMC", " store.mythia.us", "mythia.us"),
    MCL("MCLeague", "11", ChatColor.DARK_AQUA, ChatColor.YELLOW, "https://discord.gg/75x7VpV8EX", "MCLeagueEvents", " store.mcleague.net", "mcleague.net");

    private final String serverName;
    private final String serverId;

    private final ChatColor mainColor;
    private final ChatColor secondaryColor;

    private final String discordLink;
    private final String storeLink;
    private final String twitterLink;
    private final String websiteLink;

    @ConstructorProperties({"serverName", "serverId", "mainColor", "secondaryColor", "generalPrefix", "discordLink", "storeLink", "websiteLink", "mainOwner", "mainDeveloper"})
    ServerType(String serverName, String serverId, ChatColor mainColor, ChatColor secondaryColor, String discordLink, String twitterLink, String storeLink, String websiteLink) {
        this.serverName = serverName;
        this.serverId = serverId;
        this.mainColor = mainColor;
        this.secondaryColor = secondaryColor;
        this.discordLink = discordLink;
        this.twitterLink = twitterLink;
        this.storeLink = storeLink;
        this.websiteLink = websiteLink;
    }
}
