package vip.potclub.core.enums;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.beans.ConstructorProperties;
import java.util.UUID;

@Getter
public enum ServerType {

    POTCLUBVIP("PotClub", "01", ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, "", "https://dsc.gg/pot", "PotClubVIP", "store.potclub.vip", "eu.potclub.vip", UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436"), UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436")),
    EVENTIS("Eventis", "03", ChatColor.DARK_AQUA, ChatColor.AQUA, "", "https://discord.eventis.club", "EventisClubMC", "store.eventis.club", "eventis.club", UUID.fromString("c9647f7a-abfb-41c4-a8ce-a07d74434ca6"), UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436")),
    SCANDIUM("Scandium", "04", ChatColor.DARK_RED, ChatColor.RED, "", "https://solexgames.com", "SolexDev", "store.solexgames.com", "solexgames.com", UUID.fromString("c9647f7a-abfb-41c4-a8ce-a07d74434ca6"), UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436")),
    EYTRILORG("Eytril", "05", ChatColor.GOLD, ChatColor.WHITE, "", "https://discord.eytril.org/", "Eytril Network", "store.eytril.org", "eytril.org", UUID.fromString("c3a45b41-f69b-4bf1-8a0e-d63ae5135782"), UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436"));

    private final String serverName;
    private final String serverId;

    private final ChatColor mainColor;
    private final ChatColor secondaryColor;

    private final String generalPrefix;
    private final String discordLink;
    private final String storeLink;
    private final String twitterLink;
    private final String websiteLink;

    private final UUID mainOwner;
    private final UUID mainDeveloper;

    @ConstructorProperties({"serverName", "serverId", "mainColor", "secondaryColor", "generalPrefix", "discordLink", "storeLink", "websiteLink", "mainOwner", "mainDeveloper"})
    ServerType(String serverName, String serverId, ChatColor mainColor, ChatColor secondaryColor, String generalPrefix, String discordLink, String twitterLink, String storeLink, String websiteLink, UUID mainOwner, UUID mainDeveloper) {
        this.serverName = serverName;
        this.serverId = serverId;
        this.mainColor = mainColor;
        this.secondaryColor = secondaryColor;
        this.generalPrefix = generalPrefix;
        this.discordLink = discordLink;
        this.twitterLink = twitterLink;
        this.storeLink = storeLink;
        this.websiteLink = websiteLink;
        this.mainOwner = mainOwner;
        this.mainDeveloper = mainDeveloper;
    }
}
