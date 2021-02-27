package com.solexgames.core.player.punishment;

import com.solexgames.core.CorePlugin;

public final class PunishmentStrings {

    public static String BAN_MESSAGE_TEMP = "&cYou are &4banned &cfrom " + CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + " for <time>.\n&cYou were banned for: &7<reason>\n&7If you feel this ban is unjustified, fill out an appeal at " + CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink() + ".\n&6You may also purchase an unban at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + ".";
    public static String BAN_MESSAGE_PERM = "&cYou are permanently &4banned &cfrom " + CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + ".\n&cYou were &4banned&c for: &7<reason>\n&7If you feel this ban is unjustified, fill out an appeal at " + CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink() + ".\n&6You may also purchase an unban at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + ".";

    public static String MUTE_MESSAGE = "&cYou cannot speak as you are currently muted.";
    public static String KICK_MESSAGE = "&cYou were kicked for: &7<reason>";
    public static String BLACK_LIST_MESSAGE = "&4You are blacklisted from " + CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + ".&7\n&cYou were blacklisted for: &7<reason>\n&7This punishment cannot be appealed or purchased.";

    public static String SLOW_CHAT_MESSAGE = "&cPlease wait another <amount> before chatting again.";
    public static String CMD_CHAT_MESSAGE = "&cYou're on command cooldown, please wait <amount>.";
    public static String COOL_DOWN_MESSAGE = "&cPlease wait before chatting again.";

}
