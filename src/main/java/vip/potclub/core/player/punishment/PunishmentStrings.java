package vip.potclub.core.player.punishment;

import vip.potclub.core.CorePlugin;

public final class PunishmentStrings {



    public static String BAN_MESSAGE_TEMP = "&cYou are banned from " + CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + " for <time>.\n&cYou were banned for: &7<reason>\n&7If you feel this ban is unjustified, fill out a support ticket at " + CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink() + ".\n&6You may also purchase an unban at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + ".";
    public static String BAN_MESSAGE_PERM = "&cYou are permanently banned from " + CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + ".\n&cYou were banned for: &7<reason>\n&7If you feel this ban is unjustified, fill out a support ticket at " + CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink() + ".\n&6You may also purchase an unban at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + ".";

    public static String MUTE_MESSAGE_TEMP = "&cYou are muted from " + CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + " for <time>.\n&cYou were muted for: &7<reason>\n&6You may also purchase an unmute at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + ".";
    public static String MUTE_MESSAGE_PERM = "&cYou are permanently muted from " + CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + " for <time>.\n&cYou were muted for: &7<reason>\n&6You may also purchase an unmute at " + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink() + ".";

    public static String KICK_MESSAGE = "&cYou were kicked for: &7<reason>";
    public static String WARN_MESSAGE = "&cYou were warned for: &7<reason>";
    public static String BLCK_MESSAGE = "&4You were blacklisted from " + CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + ".&7\n&cYou were blacklisted for: &7<reason>\n&6This punishment cannot be appealed.";

}
