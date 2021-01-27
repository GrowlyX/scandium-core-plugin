package vip.potclub.core.player.media;

public class MediaConstants {

    public static String DISCORD_USERNAME_REGEX = "^((.{2,32})#\\d{4})";
    public static String TWITTER_USERNAME_REGEX = "(?<=^|(?<=[^a-zA-Z0-9-_\\.]))@([A-Za-z]+[A-Za-z0-9-_]+)";
    public static String INSTAGRAM_USERNAME_REGEX = "(?<=^|(?<=[^a-zA-Z0-9-_\\.]))@([A-Za-z]+[A-Za-z0-9-_]+)";
    public static String YOUTUBE_PROFILELINK_REGEX = "((http|https):\\/\\/|)(www\\.|)youtube\\.com\\/(channel\\/|user\\/|c\\/|u\\/)[a-zA-Z0-9\\-]{1,}";

}
