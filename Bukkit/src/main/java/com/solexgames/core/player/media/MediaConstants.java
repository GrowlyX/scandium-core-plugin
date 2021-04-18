package com.solexgames.core.player.media;

import java.util.regex.Pattern;

public class MediaConstants {

    public final static Pattern DISCORD_USERNAME_REGEX = Pattern.compile("^((.{2,32})#\\d{4})");
    public final static Pattern TWITTER_USERNAME_REGEX = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_]))@([A-Za-z]+[A-Za-z0-9-_]+)");
    public final static Pattern INSTAGRAM_USERNAME_REGEX = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_]))@([A-Za-z]+[A-Za-z0-9-_]+)");
    public final static Pattern YOUTUBE_PROFILE_LINK_REGEX = Pattern.compile("https?:\\/\\/(?:www\\.)?youtube\\.com\\/[\\w-]+\\/[\\w-]+");

}
