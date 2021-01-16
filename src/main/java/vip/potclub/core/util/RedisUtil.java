package vip.potclub.core.util;

import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannel;
import vip.potclub.core.enums.DataPacket;
import vip.potclub.core.enums.ReportType;
import vip.potclub.core.enums.StaffUpdateType;
import vip.potclub.core.redis.RedisMessage;

public final class RedisUtil {

    public static String onChatChannel(ChatChannel chatChannel, String message, Player player) {
        return new RedisMessage(DataPacket.CHAT_CHANNEL_UPDATE)
                .setParam("CHANNEL", chatChannel.getName())
                .setParam("MESSAGE", message)
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("PLAYER", player.getDisplayName())
                .toJson();
    }

    public static String onBroadcast(String message) {
        return new RedisMessage(DataPacket.NETWORK_BROADCAST_UPDATE)
                .setParam("MESSAGE", message)
                .toJson();
    }

    public static String onDisconnect(Player player) {
        return new RedisMessage(DataPacket.PLAYER_DISCONNECT_UPDATE)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .toJson();
    }

    public static String onConnect(Player player) {
        return new RedisMessage(DataPacket.PLAYER_DISCONNECT_UPDATE)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .toJson();
    }

    public static String onHelpOp(Player player, String message) {
        return new RedisMessage(DataPacket.PLAYER_SERVER_UPDATE)
                .setParam("MESSAGE", message)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("UPDATETYPE", StaffUpdateType.HELPOP.getName())
                .toJson();
    }

    public static String onReport(Player player, Player target, ReportType reportType) {
        return new RedisMessage(DataPacket.PLAYER_SERVER_UPDATE)
                .setParam("MESSAGE", reportType.toString())
                .setParam("PLAYER", player.getDisplayName())
                .setParam("TARGET", target.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("UPDATETYPE", StaffUpdateType.REPORT.getName())
                .toJson();
    }

    public static String onFreeze(Player player, Player target) {
        return new RedisMessage(DataPacket.PLAYER_SERVER_UPDATE)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("TARGET", target.getDisplayName())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("UPDATETYPE", StaffUpdateType.FREEZE.getName())
                .toJson();
    }

    public static String onUnfreeze(Player player, Player target) {
        return new RedisMessage(DataPacket.PLAYER_SERVER_UPDATE)
                .setParam("PLAYER", player.getDisplayName())
                .setParam("TARGET", target.getName())
                .setParam("UPDATETYPE", StaffUpdateType.FREEZE.getName())
                .toJson();
    }
}
