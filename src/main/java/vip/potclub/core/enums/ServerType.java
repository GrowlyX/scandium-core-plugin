package vip.potclub.core.enums;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.beans.ConstructorProperties;
import java.util.UUID;

@Getter
public enum ServerType {

    POTCLUBVIP("PotClub", "01", ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, "", "https://discord.gg/D5svAj23R4", "store.potclub.vip", "potclub.vip", UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436"), UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436")),
    VAXELCLUB("ClubVaxel", "02", ChatColor.BLUE, ChatColor.DARK_BLUE, "", "https://discord.gg/VxteDMMsr5", "store.vaxel.club", "eu.vaxel.club", UUID.fromString("c65c09b0-2405-411f-81d3-d5827a682a84"), UUID.fromString("bbaa8e1d-af94-4aa8-980d-36d69b9de436"));

    public String serverName;
    public String serverId;

    public ChatColor mainColor;
    public ChatColor secondaryColor;

    public String generalPrefix;
    public String discordLink;
    public String storeLink;
    public String websiteLink;

    public UUID mainOwner;
    public UUID mainDeveloper;

    @ConstructorProperties({"serverName", "serverId", "mainColor", "secondaryColor", "generalPrefix", "discordLink", "storeLink", "websiteLink", "mainOwner", "mainDeveloper"})
    ServerType(String serverName, String serverId, ChatColor mainColor, ChatColor secondaryColor, String generalPrefix, String discordLink, String storeLink, String websiteLink, UUID mainOwner, UUID mainDeveloper) {
        this.serverName = serverName;
        this.serverId = serverId;
        this.mainColor = mainColor;
        this.secondaryColor = secondaryColor;
        this.generalPrefix = generalPrefix;
        this.discordLink = discordLink;
        this.storeLink = storeLink;
        this.websiteLink = websiteLink;
        this.mainOwner = mainOwner;
        this.mainDeveloper = mainDeveloper;
    }
}
