package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.redis.action.RedisAction;
import com.solexgames.core.enums.StaffUpdateType;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.redis.json.JsonAppender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

public final class RedisUtil {

    public static String getTicksPerSecondFormatted() {
        return ChatColor.GREEN + String.format("%.2f", Math.min(CorePlugin.getInstance().getTpsRunnable().getTPS(), 20.0));
    }

    public static String onServerUpdate() {
        return new JsonAppender(RedisAction.SERVER_DATA_UPDATE)
                .put("SPLITPLAYERS", (Bukkit.getOnlinePlayers().isEmpty() ? "" : Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.joining(" "))))
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("SERVER_TYPE", CorePlugin.getInstance().getConfig().getString("server-type"))
                .put("ONLINEPLAYERS", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .put("MAXPLAYERS", String.valueOf(Bukkit.getMaxPlayers()))
                .put("WHITELIST", String.valueOf(Bukkit.getServer().hasWhitelist()))
                .put("TPS", getTicksPerSecondFormatted())
                .put("TPSSIMPLE", getTicksPerSecondFormatted())
                .getAppended();
    }

    public static String onServerOffline(){
        return new JsonAppender(RedisAction.SERVER_DATA_OFFLINE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAppended();
    }

    public static String onServerOnline(){
        return new JsonAppender(RedisAction.SERVER_DATA_ONLINE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAppended();
    }

    public static String onChatChannel(ChatChannelType chatChannel, String message, Player player) {
        return new JsonAppender(RedisAction.CHAT_CHANNEL_UPDATE)
                .put("CHANNEL", chatChannel.getName())
                .put("MESSAGE", message)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("PLAYER", player.getDisplayName())
                .getAppended();
    }

    public static String onGlobalBroadcast(String message) {
        return new JsonAppender(RedisAction.NETWORK_BROADCAST_UPDATE)
                .put("MESSAGE", message)
                .getAppended();
    }

    public static String onGlobalBroadcastPermission(String message, String permission) {
        return new JsonAppender(RedisAction.NETWORK_BROADCAST_PERMISSION_UPDATE)
                .put("MESSAGE", message)
                .put("PERMISSION", permission)
                .getAppended();
    }

    public static String onDisconnect(String player) {
        return new JsonAppender(RedisAction.PLAYER_DISCONNECT_UPDATE)
                .put("PLAYER", player)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAppended();
    }

    public static String onSwitchServer(String player, String newServer) {
        return new JsonAppender(RedisAction.PLAYER_SERVER_SWITCH_UPDATE)
                .put("PLAYER", player)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("NEW_SERVER", newServer)
                .getAppended();
    }

    public static String onConnect(Player player) {
        return new JsonAppender(RedisAction.PLAYER_CONNECT_UPDATE)
                .put("PLAYER", player.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAppended();
    }

    public static String onHelpOp(Player player, String message) {
        return new JsonAppender(RedisAction.PLAYER_SERVER_UPDATE)
                .put("MESSAGE", message)
                .put("PLAYER", player.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("UPDATETYPE", StaffUpdateType.HELPOP.getName())
                .getAppended();
    }

    public static String onReport(Player player, Player target, String message) {
        return new JsonAppender(RedisAction.PLAYER_SERVER_UPDATE)
                .put("MESSAGE", message)
                .put("PLAYER", player.getDisplayName())
                .put("TARGET", target.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("UPDATETYPE", StaffUpdateType.REPORT.getName())
                .getAppended();
    }

    public static String onFreeze(Player player, Player target) {
        return new JsonAppender(RedisAction.PLAYER_SERVER_UPDATE)
                .put("PLAYER", player.getDisplayName())
                .put("TARGET", target.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("UPDATETYPE", StaffUpdateType.FREEZE.getName())
                .getAppended();
    }

    public static String updateRanks() {
        return new JsonAppender(RedisAction.RANK_SETTINGS_UPDATE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAppended();
    }

    public static String executePunishment(PunishmentType punishmentType, UUID issuer, UUID target, String issuerName, String reason, Date issuingDate, long punishmentDuration, boolean permanent, Date createdAt, UUID uuid, String punishIdentification, boolean silent) {
        return new JsonAppender(RedisAction.PUNISHMENT_EXECUTE_UPDATE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("TYPE", punishmentType.toString())
                .put("ISSUER", (issuer != null ? issuer.toString() : null))
                .put("TARGET", target.toString())
                .put("ISSUERNAME", issuerName)
                .put("REASON", reason)
                .put("DATE", String.valueOf(issuingDate.getTime()))
                .put("DURATION", String.valueOf(punishmentDuration))
                .put("PERMANENT", String.valueOf(permanent))
                .put("CREATED", String.valueOf(createdAt.getTime()))
                .put("UUID", uuid.toString())
                .put("IDENTIFICATION", punishIdentification)
                .put("SILENT", String.valueOf(silent))
                .getAppended();
    }

    public static String fRemovePunishment(Punishment punishment) {
        return new JsonAppender(RedisAction.PUNISHMENT_F_REMOVE_UPDATE)
                .put("ID", punishment.getPunishIdentification())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAppended();
    }

    public static String removePunishment(Player remover, Punishment punishment, String message) {
        if (remover != null) {
            return new JsonAppender(RedisAction.PUNISHMENT_REMOVE_UPDATE)
                    .put("REMOVERUUID", remover.getUniqueId().toString())
                    .put("REMOVERNAME", remover.getName())
                    .put("REMOVERDISPLAYNAME", remover.getDisplayName())
                    .put("REASON", message)
                    .put("SERVER", CorePlugin.getInstance().getServerName())
                    .put("ID", punishment.getPunishIdentification())
                    .getAppended();
        } else {
            return new JsonAppender(RedisAction.PUNISHMENT_REMOVE_UPDATE)
                    .put("REMOVERUUID", null)
                    .put("REMOVERNAME", null)
                    .put("REMOVERDISPLAYNAME", null)
                    .put("REASON", message)
                    .put("SERVER", CorePlugin.getInstance().getServerName())
                    .put("ID", punishment.getPunishIdentification())
                    .getAppended();
        }
    }

    public static String createRank(String name, Player player, String uuid) {
        return new JsonAppender(RedisAction.RANK_CREATE_UPDATE)
                .put("NAME", name)
                .put("UUID", uuid)
                .put("PLAYER", player.getName())
                .getAppended();
    }

    public static String deleteRank(String rank, Player player) {
        return new JsonAppender(RedisAction.RANK_DELETE_UPDATE)
                .put("RANK", rank)
                .put("PLAYER", player.getName())
                .getAppended();
    }

    public static String onUnfreeze(Player player, Player target) {
        return new JsonAppender(RedisAction.PLAYER_SERVER_UPDATE)
                .put("PLAYER", player.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("TARGET", target.getDisplayName())
                .put("UPDATETYPE", StaffUpdateType.UNFREEZE.getName())
                .getAppended();
    }

    public static String addGlobalPlayer(PotPlayer potPlayer) {
        return new JsonAppender(RedisAction.GLOBAL_PLAYER_ADDITION)
                .put("UUID", potPlayer.getUuid().toString())
                .put("NAME", potPlayer.getName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("RANK", potPlayer.getActiveGrant().getRank().getName())
                .put("DMS_ENABLED", String.valueOf(potPlayer.isCanReceiveDms()))
                .getAppended();
    }

    public static String removeGlobalPlayer(UUID uuid) {
        return new JsonAppender(RedisAction.GLOBAL_PLAYER_REMOVE)
                .put("UUID", uuid.toString())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAppended();
    }

    public static void writeAsync(String message) {
        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisManager().write(message));
    }

    public static void write(String message) {
        CorePlugin.getInstance().getRedisManager().write(message);
    }
}
