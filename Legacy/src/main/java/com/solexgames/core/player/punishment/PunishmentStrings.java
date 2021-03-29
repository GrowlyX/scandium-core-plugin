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

    public static String BAN_MESSAGE_TEMP =
            "&cYou are &4banned &cfrom " + SERVER_NAME + " for <time>." +
                    "\n&cYou were banned for: &7<reason>" +
                    "\n&7If you feel this ban is unjustified, fill out an appeal at " + DISCORD_LINK + "." +
                    "\n&6You may also purchase an unban at " + STORE_LINK + ".";
    public static String BAN_MESSAGE_PERM =
            "&cYou are permanently &4banned &cfrom " + SERVER_NAME + "." +
                    "\n&cYou were &4banned&c for: &7<reason>" +
                    "\n&7If you feel this ban is unjustified, fill out an appeal at " + DISCORD_LINK + "." +
                    "\n&6You may also purchase an unban at " + STORE_LINK + ".";
    public static String BLACK_LIST_MESSAGE =
            "&4You are blacklisted from " + SERVER_NAME + ".&7" +
                    "\n&cYou were blacklisted for: &7<reason>" +
                    "\n&7This punishment cannot be appealed or purchased.";

    public static String MUTE_MESSAGE = "&cYou cannot speak as you are currently muted.";
    public static String KICK_MESSAGE = "&cYou were kicked for: &7<reason>";

    public static String SLOW_CHAT_MESSAGE = "&cPlease wait another <amount> before chatting again.";
    public static String CMD_CHAT_MESSAGE = "&cYou're on command cooldown, please wait <amount>.";
    public static String COOL_DOWN_MESSAGE = "&cPlease wait before chatting again.";

    public static String PLAYER_DATA_LOAD = ChatColor.RED + "An error occurred while trying to load your data.\n" + ChatColor.RED + "Please try again later or contact a staff member.";

    /**
     * Setups customized messages through the config.
     */
    public void setupMessages() {
        FileConfiguration configuration = JavaPlugin.getPlugin(CorePlugin.class).getConfig();

        PLAYER_DATA_LOAD = Color.translate(configuration.getString("messages.load-error").replace("<nl>", "\n"));
        SLOW_CHAT_MESSAGE = Color.translate(configuration.getString("messages.chat-cooldown").replace("<nl>", "\n"));
        CMD_CHAT_MESSAGE = Color.translate(configuration.getString("messages.command-cooldown").replace("<nl>", "\n"));
        COOL_DOWN_MESSAGE = Color.translate(configuration.getString("messages.chat-delay-cooldown").replace("<nl>", "\n"));
        KICK_MESSAGE = Color.translate(configuration.getString("messages.kick-message").replace("<nl>", "\n"));
        MUTE_MESSAGE = Color.translate(configuration.getString("messages.mute-restricted").replace("<nl>", "\n"));
    }
}
