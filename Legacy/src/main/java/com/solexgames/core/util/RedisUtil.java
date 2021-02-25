package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.RedisPacketType;
import com.solexgames.core.enums.StaffUpdateType;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.redis.RedisMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public final class RedisUtil {

    public static String getTicksPerSecondFormatted() {
        return ChatColor.GREEN + String.format("%.2f", Math.min(CorePlugin.getInstance().getTpsRunnable().getTPS(), 20.0));
    }

    public static String onServerUpdate() {
        return new RedisMessage(RedisPacketType.SERVER_DATA_UPDATE)
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
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
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .toJson();
    }

    public static String onServerOnline(){
        return new RedisMessage(RedisPacketType.SERVER_DATA_ONLINE)
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
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
        return new RedisMessage(RedisPacketType.NETWORK_BROADCAST_PERMISSION_UPDATE)
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
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .toJson();
    }

    public static String executePunishment(PunishmentType punishmentType, UUID issuer, UUID target, String issuerName, String reason, Date issuingDate, long punishmentDuration, boolean permanent, Date createdAt, UUID uuid, String punishIdentification, boolean silent) {
        return new RedisMessage(RedisPacketType.PUNISHMENT_EXECUTE_UPDATE)
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("TYPE", punishmentType.toString())
                .setParam("ISSUER", (issuer != null ? issuer.toString() : null))
                .setParam("TARGET", target.toString())
                .setParam("ISSUERNAME", issuerName)
                .setParam("REASON", reason)
                .setParam("DATE", String.valueOf(issuingDate.getTime()))
                .setParam("DURATION", String.valueOf(punishmentDuration))
                .setParam("PERMANENT", String.valueOf(permanent))
                .setParam("CREATED", String.valueOf(createdAt.getTime()))
                .setParam("UUID", uuid.toString())
                .setParam("IDENTIFICATION", punishIdentification)
                .setParam("SILENT", String.valueOf(silent))
                .toJson();
    }

    public static String fRemovePunishment(Punishment punishment) {
        return new RedisMessage(RedisPacketType.PUNISHMENT_FREMOVE_UPDATE)
                .setParam("ID", punishment.getPunishIdentification())
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .toJson();
    }

    public static String removePunishment(Player remover, Punishment punishment, String message) {
        if (remover != null) {
            return new RedisMessage(RedisPacketType.PUNISHMENT_REMOVE_UPDATE)
                    .setParam("REMOVERUUID", remover.getUniqueId().toString())
                    .setParam("REMOVERNAME", remover.getName())
                    .setParam("REMOVERDISPLAYNAME", remover.getDisplayName())
                    .setParam("REASON", message)
                    .setParam("SERVER", CorePlugin.getInstance().getServerName())
                    .setParam("ID", punishment.getPunishIdentification())
                    .toJson();
        } else {
            return new RedisMessage(RedisPacketType.PUNISHMENT_REMOVE_UPDATE)
                    .setParam("REMOVERUUID", null)
                    .setParam("REMOVERNAME", null)
                    .setParam("REMOVERDISPLAYNAME", null)
                    .setParam("REASON", message)
                    .setParam("SERVER", CorePlugin.getInstance().getServerName())
                    .setParam("ID", punishment.getPunishIdentification())
                    .toJson();
        }
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
                .setParam("SERVER", CorePlugin.getInstance().getServerName())
                .setParam("TARGET", target.getDisplayName())
                .setParam("UPDATETYPE", StaffUpdateType.UNFREEZE.getName())
                .toJson();
    }

    public static void writeAsync(String message) {
        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(message));
    }

    public static void write(String message) {
        CorePlugin.getInstance().getRedisClient().write(message);
    }
}
