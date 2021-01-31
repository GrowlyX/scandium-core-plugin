package vip.potclub.core.player.media;

import java.util.regex.Pattern;

public class MediaConstants {

    public static Pattern DISCORD_USERNAME_REGEX = Pattern.compile("^((.{2,32})#\\d{4})");
    public static Pattern TWITTER_USERNAME_REGEX = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_]))@([A-Za-z]+[A-Za-z0-9-_]+)");
    public static Pattern INSTAGRAM_USERNAME_REGEX = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_]))@([A-Za-z]+[A-Za-z0-9-_]+)");
    public static Pattern YOUTUBE_PROFILELINK_REGEX = Pattern.compile("https?:\\/\\/(?:www\\.)?youtube\\.com\\/[\\w-]+\\/[\\w-]+");

}
