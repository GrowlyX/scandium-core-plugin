package vip.potclub.core.enums;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.beans.ConstructorProperties;
import java.util.UUID;

@Getter
public enum ServerType {

    POTCLUBVIP("PotClub", "01", ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, "", "https://discord.gg/D5svAj23R4", "PotClubVIP", "store.potclub.vip", "potclub.vip", UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436"), UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436")),
    ZONIXUS("Zonix", "01", ChatColor.DARK_RED, ChatColor.RED, "", "https://discord.gg/zonix", "ZonixUS", "store.zonix.us", "zonix.us", UUID.fromString("0a786830-2691-4eb2-8a0e-6e2c36a545a1"), UUID.fromString("4ec1957e-1635-4234-ad17-a3de963dacde")),
    VAXELCLUB("ClubVaxel", "02", ChatColor.BLUE, ChatColor.DARK_BLUE, "", "https://discord.gg/VxteDMMsr5", "ClubVaxel", "store.vaxel.club", "eu.vaxel.club", UUID.fromString("c65c09b0-2405-411f-81d3-d5827a682a84"), UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436"));

    public String serverName;
    public String serverId;

    public ChatColor mainColor;
    public ChatColor secondaryColor;

    public String generalPrefix;
    public String discordLink;
    public String storeLink;
    public String twitterLink;
    public String websiteLink;

    public UUID mainOwner;
    public UUID mainDeveloper;

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
