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

public final class RedisUtil {

    private static String format(double tps) {
        return ( ( tps > 18.0 ) ? ChatColor.GREEN : ( tps > 16.0 ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > 20.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
    }

    public static String getTicksPerSecond() {
        double[] tps = Bukkit.spigot().getTPS();
        String[] tpsAvg = new String[tps.length];

        for (int i = 0; i < tps.length; i++) {
            tpsAvg[i] = format(tps[i]);
        }

        return StringUtils.join(tpsAvg, ChatColor.GRAY +  ", " + ChatColor.GREEN);
    }

    public static String getTicksPerSecondSimplified() {
        double[] tps = Bukkit.spigot().getTPS();

        return ChatColor.GREEN + String.valueOf(Math.min(Math.round(tps[0] * 100.0 ) / 100.0, 20.0));
    }

    public static String onServerUpdate() {
        return new RedisMessage(RedisPacketType.SERVER_DATA_UPDATE)
                .setParam("SERVER", CorePlugin.getInstance().getConfig().getString("server-id"))
                .setParam("SERVER_TYPE", CorePlugin.getInstance().getConfig().getString("server-type"))
                .setParam("ONLINEPLAYERS", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .setParam("MAXPLAYERS", String.valueOf(Bukkit.getMaxPlayers()))
                .setParam("WHITELIST", String.valueOf(Bukkit.getServer().hasWhitelist()))
                .setParam("TPS", getTicksPerSecond())
                .setParam("TPSSIMPLE", getTicksPerSecondSimplified())
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

    public static String onUnfreeze(Player player, Player target) {
        return new RedisMessage(RedisPacketType.PLAYER_SERVER_UPDATE)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("TARGET", target.getName())
                .setParam("UPDATETYPE", StaffUpdateType.FREEZE.getName())
                .toJson();
    }
}
