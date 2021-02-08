package vip.potclub.core.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.enums.RedisPacketType;
import vip.potclub.core.enums.StaffUpdateType;
import vip.potclub.core.redis.RedisMessage;

import java.util.UUID;

public final class RedisUtil {

    public static String getTicksPerSecondFormatted() {
        return ChatColor.GREEN + String.format("%.2f", Math.min(CorePlugin.getInstance().getTpsRunnable().getTPS(), 20.0));
    }

    public static String onServerUpdate() {
        return new RedisMessage(RedisPacketType.SERVER_DATA_UPDATE)
                .setParam("SERVER", CorePlugin.getInstance().getConfig().getString("server-id"))
                .setParam("SERVER_TYPE", CorePlugin.getInstance().getConfig().getString("server-type"))
                .setParam("ONLINEPLAYERS", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .setParam("MAXPLAYERS", String.valueOf(Bukkit.getMaxPlayers()))
                .setParam("WHITELIST", String.valueOf(Bukkit.getServer().hasWhitelist()))
                .setParam("TPS", getTicksPerSecondFormatted())
                .setParam("TPSSIMPLE", getTicksPerSecondFormatted())
                .toJson();
    }

    public static String onServerOffline(){
        return new RedisMessage(RedisPacketType.SERVER_DATA_OFFLINE)
                .setParam("SERVER", CorePlugin.getInstance().getConfig().getString("server-id"))
                .toJson();
    }

    public static String onServerOnline(){
        return new RedisMessage(RedisPacketType.SERVER_DATA_ONLINE)
                .setParam("SERVER", CorePlugin.getInstance().getConfig().getString("server-id"))
                .toJson();
    }

    public static String onChatChannel(ChatChannelType chatChannel, String message, Player player) {
        return new RedisMessage(RedisPacketType.CHAT_CHANNEL_UPDATE)
                .setParam("CHANNEL", chatChannel.getName())
                .setParam("MESSAGE", message)
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("PLAYER", player.getDisplayName())
                .toJson();
    }

    public static String onGlobalBroadcast(String message) {
        return new RedisMessage(RedisPacketType.NETWORK_BROADCAST_UPDATE)
                .setParam("MESSAGE", message)
                .toJson();
    }

    public static String onGlobalBroadcastPermission(String message, String permission) {
        return new RedisMessage(RedisPacketType.NETWORK_BROADCAST_UPDATE)
                .setParam("MESSAGE", message)
                .setParam("PERMISSION", permission)
                .toJson();
    }

    public static String onDisconnect(Player player) {
        return new RedisMessage(RedisPacketType.PLAYER_DISCONNECT_UPDATE)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .toJson();
    }

    public static String onConnect(Player player) {
        return new RedisMessage(RedisPacketType.PLAYER_CONNECT_UPDATE)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .toJson();
    }

    public static String onHelpOp(Player player, String message) {
        return new RedisMessage(RedisPacketType.PLAYER_SERVER_UPDATE)
                .setParam("MESSAGE", message)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("UPDATETYPE", StaffUpdateType.HELPOP.getName())
                .toJson();
    }

    public static String onReport(Player player, Player target, String message) {
        return new RedisMessage(RedisPacketType.PLAYER_SERVER_UPDATE)
                .setParam("MESSAGE", message)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("TARGET", target.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("UPDATETYPE", StaffUpdateType.REPORT.getName())
                .toJson();
    }

    public static String onFreeze(Player player, Player target) {
        return new RedisMessage(RedisPacketType.PLAYER_SERVER_UPDATE)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("TARGET", target.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("UPDATETYPE", StaffUpdateType.FREEZE.getName())
                .toJson();
    }

    public static String updateRanks() {
        return new RedisMessage(RedisPacketType.RANK_SETTINGS_UPDATE)
                .setParam("SERVER", CorePlugin.getInstance().getConfig().getString("server-id"))
                .toJson();
    }

    public static String createRank(String name, Player player, String uuid) {
        return new RedisMessage(RedisPacketType.RANK_CREATE_UPDATE)
                .setParam("NAME", name)
                .setParam("UUID", uuid)
                .setParam("PLAYER", player.getName())
                .toJson();
    }

    public static String deleteRank(String rank, Player player) {
        return new RedisMessage(RedisPacketType.RANK_DELETE_UPDATE)
                .setParam("RANK", rank)
                .setParam("PLAYER", player.getName())
                .toJson();
    }

    public static String onUnfreeze(Player player, Player target) {
        return new RedisMessage(RedisPacketType.PLAYER_SERVER_UPDATE)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("TARGET", target.getName())
                .setParam("UPDATETYPE", StaffUpdateType.FREEZE.getName())
                .toJson();
    }

    public static void writeAsync(String message) {
        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(message));
    }

    public static void write(String message) {
        CorePlugin.getInstance().getRedisClient().write(message);
    }
}
