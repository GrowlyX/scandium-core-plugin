package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.disguise.DisguiseData;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.StaffUpdateType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.redis.JedisAction;
import com.solexgames.lib.commons.redis.json.JsonAppender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
        return new JsonAppender(JedisAction.SERVER_DATA_UPDATE)
                .put("SPLITPLAYERS", (Bukkit.getOnlinePlayers().isEmpty() ? "" : Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.joining(" "))))
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("SERVER_TYPE", CorePlugin.getInstance().getConfig().getString("server.group"))
                .put("ONLINEPLAYERS", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .put("MAXPLAYERS", String.valueOf(Bukkit.getMaxPlayers()))
                .put("WHITELIST", String.valueOf(Bukkit.getServer().hasWhitelist()))
                .put("TPS", getTicksPerSecondFormatted())
                .put("TPSSIMPLE", getTicksPerSecondFormatted())
                .getAsJson();
    }

    public static String onDisguiseProfileCreate(DisguiseData disguiseData) {
        return new JsonAppender(JedisAction.DISGUISE_PROFILE_CREATE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("NAME", disguiseData.getName())
                .put("SKIN", disguiseData.getSkin())
                .put("SIGNATURE", disguiseData.getSignature())
                .put("UUID", disguiseData.getUuid().toString())
                .getAsJson();
    }

    public static String onDisguiseProfileRemove(DisguiseData disguiseData) {
        return new JsonAppender(JedisAction.DISGUISE_PROFILE_CREATE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("UUID", disguiseData.getUuid().toString())
                .getAsJson();
    }

    public static String onServerOffline() {
        return new JsonAppender(JedisAction.SERVER_DATA_OFFLINE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAsJson();
    }

    public static String onServerOnline() {
        return new JsonAppender(JedisAction.SERVER_DATA_ONLINE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAsJson();
    }

    public static String onChatChannel(ChatChannelType chatChannel, String message, Player player) {
        return new JsonAppender(JedisAction.CHAT_CHANNEL_UPDATE)
                .put("CHANNEL", chatChannel.name())
                .put("MESSAGE", message)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("PLAYER", player.getDisplayName())
                .getAsJson();
    }

    public static String onGlobalBroadcast(String message) {
        return new JsonAppender(JedisAction.NETWORK_BROADCAST_UPDATE)
                .put("MESSAGE", message)
                .getAsJson();
    }

    public static String onGlobalBroadcastPermission(String message, String permission) {
        return new JsonAppender(JedisAction.NETWORK_BROADCAST_PERMISSION_UPDATE)
                .put("MESSAGE", message)
                .put("PERMISSION", permission)
                .getAsJson();
    }

    public static String onDisconnect(String player) {
        return new JsonAppender(JedisAction.PLAYER_DISCONNECT_UPDATE)
                .put("PLAYER", player)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAsJson();
    }

    public static String onSwitchServer(String player, String newServer) {
        return new JsonAppender(JedisAction.PLAYER_SERVER_SWITCH_UPDATE)
                .put("PLAYER", player)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("NEW_SERVER", newServer)
                .getAsJson();
    }

    public static String onConnect(Player player) {
        return new JsonAppender(JedisAction.PLAYER_CONNECT_UPDATE)
                .put("PLAYER", player.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAsJson();
    }

    public static String onHelpOp(Player player, String message) {
        return new JsonAppender(JedisAction.PLAYER_SERVER_UPDATE)
                .put("MESSAGE", message)
                .put("PLAYER", player.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("UPDATETYPE", StaffUpdateType.REQUEST.name())
                .getAsJson();
    }

    public static String onReport(Player player, Player target, String message) {
        return new JsonAppender(JedisAction.PLAYER_SERVER_UPDATE)
                .put("MESSAGE", message)
                .put("PLAYER", player.getDisplayName())
                .put("TARGET", target.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("UPDATETYPE", StaffUpdateType.REPORT.name())
                .getAsJson();
    }

    public static String onFreeze(Player player, Player target) {
        return new JsonAppender(JedisAction.PLAYER_SERVER_UPDATE)
                .put("PLAYER", player.getDisplayName())
                .put("TARGET", target.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("UPDATETYPE", StaffUpdateType.FREEZE.name())
                .getAsJson();
    }

    public static String updateRank(Rank rank) {
        return new JsonAppender(JedisAction.RANK_SETTINGS_UPDATE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("RANK", rank.getName())
                .put("WEIGHT", String.valueOf(rank.getWeight()))
                .put("COLOR", rank.getColor())
                .put("PREFIX", rank.getPrefix())
                .put("DEFAULT", String.valueOf(rank.isDefaultRank()))
                .put("HIDDEN", String.valueOf(rank.isHidden()))
                .put("SUFFIX", rank.getSuffix())
                .put("PURCHASABLE", String.valueOf(rank.isPurchasable()))
                .put("ITALIC", String.valueOf(rank.isItalic()))
                .put("PERMISSIONS", String.join(" ", rank.getPermissions()))
                .put("INHERITANCE", rank.getInheritance().stream().map(UUID::toString).collect(Collectors.joining(" ")))
                .getAsJson();
    }

    public static String updatePrefix(Prefix prefix) {
        return new JsonAppender(JedisAction.PREFIX_SETTINGS_UPDATE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("PREFIX", prefix.getName())
                .put("ID", prefix.getId())
                .put("DESIGN", prefix.getPrefix())
                .getAsJson();
    }

    public static String deletePrefix(Prefix prefix) {
        return new JsonAppender(JedisAction.PREFIX_DELETE_UPDATE)
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("PREFIX", prefix.getName())
                .getAsJson();
    }

    public static String executePunishment(PunishmentType punishmentType, UUID issuer, UUID target, String issuerName, String reason, Date issuingDate, long punishmentDuration, boolean permanent, Date createdAt, UUID uuid, String punishIdentification, boolean silent) {
        return new JsonAppender(JedisAction.PUNISHMENT_EXECUTE_UPDATE)
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
                .getAsJson();
    }

    public static String fRemovePunishment(Punishment punishment) {
        return new JsonAppender(JedisAction.PUNISHMENT_F_REMOVE_UPDATE)
                .put("ID", punishment.getPunishIdentification())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAsJson();
    }

    public static String removePunishment(Player remover, Punishment punishment, String message) {
        if (remover != null) {
            return new JsonAppender(JedisAction.PUNISHMENT_REMOVE_UPDATE)
                    .put("REMOVERUUID", remover.getUniqueId().toString())
                    .put("REMOVERNAME", remover.getName())
                    .put("REMOVERDISPLAYNAME", remover.getDisplayName())
                    .put("REASON", message)
                    .put("SERVER", CorePlugin.getInstance().getServerName())
                    .put("ID", punishment.getPunishIdentification())
                    .getAsJson();
        } else {
            return new JsonAppender(JedisAction.PUNISHMENT_REMOVE_UPDATE)
                    .put("REMOVERUUID", (String) null)
                    .put("REMOVERNAME", (String) null)
                    .put("REMOVERDISPLAYNAME", (String) null)
                    .put("REASON", message)
                    .put("SERVER", CorePlugin.getInstance().getServerName())
                    .put("ID", punishment.getPunishIdentification())
                    .getAsJson();
        }
    }

    public static String createRank(String name, Player player, String uuid) {
        return new JsonAppender(JedisAction.RANK_CREATE_UPDATE)
                .put("NAME", name)
                .put("UUID", uuid)
                .put("PLAYER", player.getName())
                .getAsJson();
    }

    public static String deleteRank(String rank, Player player) {
        return new JsonAppender(JedisAction.RANK_DELETE_UPDATE)
                .put("RANK", rank)
                .put("PLAYER", player.getName())
                .getAsJson();
    }

    public static String onUnfreeze(Player player, Player target) {
        return new JsonAppender(JedisAction.PLAYER_SERVER_UPDATE)
                .put("PLAYER", player.getDisplayName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("TARGET", target.getDisplayName())
                .put("UPDATETYPE", StaffUpdateType.UNFREEZE.name())
                .getAsJson();
    }

    public static String addGlobalPlayer(PotPlayer potPlayer) {
        return new JsonAppender(JedisAction.GLOBAL_PLAYER_ADDITION)
                .put("UUID", potPlayer.getUuid().toString())
                .put("NAME", potPlayer.getName())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .put("RANK", potPlayer.getActiveGrant().getRank().getName())
                .put("SYNC_CODE", potPlayer.getSyncCode())
                .put("IS_SYNCED", String.valueOf(potPlayer.isSynced()))
                .put("DMS_ENABLED", String.valueOf(potPlayer.isCanReceiveDms()))
                .getAsJson();
    }

    public static String removeGlobalPlayer(UUID uuid) {
        return new JsonAppender(JedisAction.GLOBAL_PLAYER_REMOVE)
                .put("UUID", uuid.toString())
                .put("SERVER", CorePlugin.getInstance().getServerName())
                .getAsJson();
    }

    public static String syncDiscord(String discord, String name) {
        return new JsonAppender(JedisAction.DISCORD_SYNC_UPDATE)
                .put("DISCORD", discord)
                .put("NAME", name)
                .getAsJson();
    }

    public static void publishAsync(String message) {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getJedisManager().publish(message));
    }

    public static void publishSync(String message) {
        CorePlugin.getInstance().getJedisManager().publish(message);
    }
}
