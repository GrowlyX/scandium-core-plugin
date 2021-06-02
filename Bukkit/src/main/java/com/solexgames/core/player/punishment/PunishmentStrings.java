package com.solexgames.core.player.punishment;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author GrowlyX
 * @since 2021
 */

public final class PunishmentStrings {

    private final static String SERVER_NAME = CorePlugin.getInstance().getServerManager().getNetwork().getServerName();
    private final static String DISCORD_LINK = CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink();
    private final static String STORE_LINK = CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink();

    public final static String BAN_MESSAGE_TEMP =
            "&cYou are &4banned &cfrom " + SERVER_NAME + " for <time>." +
                    "\n&cYou were banned for: &7<reason>" +
                    "\n&7If you feel this ban is unjustified, crete a ticket at " + DISCORD_LINK + "." +
                    "\n&6You may also purchase an unban at " + STORE_LINK + ".";
    public final static String BAN_MESSAGE_PERM =
            "&cYou are permanently &4banned &cfrom " + SERVER_NAME + "." +
                    "\n&cYou were &4banned&c for: &7<reason>" +
                    "\n&7If you feel this ban is unjustified, crete a ticket at " + DISCORD_LINK + "." +
                    "\n&6You may also purchase an unban at " + STORE_LINK + ".";
    public final static String BLACK_LIST_MESSAGE =
            "&4You are blacklisted from " + SERVER_NAME + ".&7" +
                    "\n&cYou were blacklisted for: &7<reason>" +
                    "\n&7This punishment cannot be appealed or purchased.";
    public final static String IP_BAN_RELATION_MESSAGE =
            "&cYour IP is permanently &4banned &cfrom " + SERVER_NAME + "." +
                    "\n&cYour ban is in relation to the account: &7<player>" +
                    "\n&7If you feel this ban is unjustified, crete a ticket at " + DISCORD_LINK + "." +
                    "\n&6You can purchase an unban for <player> &6at " + STORE_LINK + ".";
    public final static String BLACK_LIST_RELATION_MESSAGE =
            "&cYour IP is permanently &4blacklisted &cfrom " + SERVER_NAME + "." +
                    "\n&cYour blacklist is in relation to the account: &7<player>" +
                    "\n&7If you feel this ban is unjustified, crete a ticket at " + DISCORD_LINK + ".";

    public static String MUTE_MESSAGE = ChatColor.RED + "You cannot speak as you are currently muted.";
    public static String KICK_MESSAGE = ChatColor.RED + "You were kicked for: " + ChatColor.GRAY + "<reason>";

    public static String SLOW_CHAT_MESSAGE = ChatColor.RED + "Chat is currently slowed, please wait <amount> before chatting again.";
    public static String CMD_CHAT_MESSAGE = ChatColor.RED + "You're on command cooldown, please wait <amount>.";
    public static String COOL_DOWN_MESSAGE = ChatColor.RED + "You're on chat cooldown, please wait <amount>.";

    public static String PLAYER_DATA_LOAD = ChatColor.RED + "An error occurred while trying to load your data.\n" + ChatColor.RED + "Please try again later or contact a staff member.";
    public static String SERVER_NOT_LOADED = ChatColor.RED + "The server you've tried to connect to has not loaded.\n" + ChatColor.RED + "Please try again in a few seconds or contact staff.";

    /**
     * Setups customized messages through the config.
     */
    public void setupMessages() {
        final FileConfiguration configuration = CorePlugin.getInstance().getConfig();

        PunishmentStrings.PLAYER_DATA_LOAD = Color.translate(configuration.getString("language.load-error").replace("<nl>", "\n"));
        PunishmentStrings.SERVER_NOT_LOADED = Color.translate(configuration.getString("language.server-not-loaded").replace("<nl>", "\n"));
        PunishmentStrings.SLOW_CHAT_MESSAGE = Color.translate(configuration.getString("language.chat-cooldown").replace("<nl>", "\n"));
        PunishmentStrings.CMD_CHAT_MESSAGE = Color.translate(configuration.getString("language.command-cooldown").replace("<nl>", "\n"));
        PunishmentStrings.COOL_DOWN_MESSAGE = Color.translate(configuration.getString("language.chat-delay-cooldown").replace("<nl>", "\n"));
        PunishmentStrings.KICK_MESSAGE = Color.translate(configuration.getString("language.kick-message").replace("<nl>", "\n"));
        PunishmentStrings.MUTE_MESSAGE = Color.translate(configuration.getString("language.mute-restricted").replace("<nl>", "\n"));
    }
}
