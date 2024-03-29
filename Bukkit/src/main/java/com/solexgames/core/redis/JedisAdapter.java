package com.solexgames.core.redis;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.disguise.DisguiseData;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.NetworkServerStatusType;
import com.solexgames.core.enums.NetworkServerType;
import com.solexgames.core.enums.StaffUpdateType;
import com.solexgames.core.listener.custom.ServerRetrieveEvent;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.lib.commons.redis.annotation.Subscription;
import com.solexgames.lib.commons.redis.handler.JedisHandler;
import com.solexgames.lib.commons.redis.json.JsonAppender;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import com.solexgames.core.util.RedisUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("unused")
public class JedisAdapter implements JedisHandler {

    @Subscription(action = "PERMISSION_SYNC")
    public void onSomething(JsonAppender jsonAppender) { }

    @Subscription(action = "PREFIX_SETTINGS_UPDATE")
    public void onPrefixSettingsUpdate(JsonAppender jsonAppender) {
        if (!jsonAppender.getParam("SERVER").equals(CorePlugin.getInstance().getServerName())) {
            final Prefix existingPrefix = Prefix.getByName(jsonAppender.getParam("PREFIX"));

            if (existingPrefix != null) {
                existingPrefix.setId(jsonAppender.getParam("ID"));
                existingPrefix.setPrefix(jsonAppender.getParam("DESIGN"));
            } else {
                final Prefix newPrefix = new Prefix(jsonAppender.getParam("PREFIX"), jsonAppender.getParam("DESIGN"));
                newPrefix.setId(jsonAppender.getParam("ID"));
            }
        }
    }

    @Subscription(action = "PREFIX_DELETE_UPDATE")
    public void onPrefixDeleteUpdate(JsonAppender jsonAppender) {
        if (!jsonAppender.getParam("SERVER").equals(CorePlugin.getInstance().getServerName())) {
            final Prefix existingPrefix = Prefix.getByName(jsonAppender.getParam("PREFIX"));

            if (existingPrefix != null) {
                Prefix.getPrefixes().remove(existingPrefix);
            }
        }
    }

    @Subscription(action = "DISGUISE_PROFILE_REMOVE")
    public void onDisguiseProfileRemove(JsonAppender jsonAppender) {
        if (!jsonAppender.getParam("SERVER").equals(CorePlugin.getInstance().getServerName())) {
            final UUID disguiseRemoveUUID = UUID.fromString(jsonAppender.getParam("UUID"));
            final DisguiseData disguiseData = CorePlugin.getInstance().getDisguiseCache().getByUuid(disguiseRemoveUUID);

            if (disguiseData != null) {
                CorePlugin.getInstance().getDisguiseCache().removeDataPair(disguiseData);
            }
        }
    }

    @Subscription(action = "DISGUISE_PROFILE_CREATE")
    public void onDisguiseProfileCreate(JsonAppender jsonAppender) {
        if (!jsonAppender.getParam("SERVER").equals(CorePlugin.getInstance().getServerName())) {
            final String disguiseName = jsonAppender.getParam("NAME");
            final String disguiseSkin = jsonAppender.getParam("SKIN");
            final String disguiseSignature = jsonAppender.getParam("SKIN");
            final UUID disguiseUUID = UUID.fromString(jsonAppender.getParam("UUID"));

            CorePlugin.getInstance().getDisguiseCache().registerNewDataPair(new DisguiseData(disguiseUUID, disguiseName, disguiseSkin, disguiseSignature));
        }
    }

    @Subscription(action = "GLOBAL_PLAYER_REMOVE")
    public void onGlobalPlayerRemove(JsonAppender jsonAppender) {
        final UUID removingPlayer = UUID.fromString(jsonAppender.getParam("UUID"));
        final String removalServer = jsonAppender.getParam("SERVER");

        if (!removalServer.equalsIgnoreCase(CorePlugin.getInstance().getServerName())) {
            final List<NetworkPlayer> networkPlayers = new ArrayList<>(CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles());

            networkPlayers.stream().filter(nPlayer -> nPlayer.getUuid().equals(removingPlayer))
                    .findFirst().ifPresent(networkPlayer -> CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().remove(networkPlayer));
        }
    }

    @Subscription(action = "GLOBAL_PLAYER_ADDITION")
    public void onGlobalPlayerAddition(JsonAppender jsonAppender) {
        final UUID uuid = UUID.fromString(jsonAppender.getParam("UUID"));
        final String name = jsonAppender.getParam("NAME");
        final Rank rank = Rank.getByName(jsonAppender.getParam("RANK"));
        final String globalServer = jsonAppender.getParam("SERVER");
        final String syncCode = jsonAppender.getParam("SYNC_CODE");

        final boolean dmsEnabled = Boolean.parseBoolean(jsonAppender.getParam("DMS_ENABLED"));
        final boolean isSynced = Boolean.parseBoolean(jsonAppender.getParam("IS_SYNCED"));

        final NetworkPlayer networkPlayer = new NetworkPlayer(uuid, "", name, globalServer, rank.getName(), syncCode, dmsEnabled, isSynced, System.currentTimeMillis());

        if (CorePlugin.getInstance().getPlayerManager().isOnline(name)) {
            final NetworkPlayer oldPlayer = CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(name);

            oldPlayer.setServerName(globalServer);
            oldPlayer.setSynced(isSynced);
        } else {
            CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().add(networkPlayer);
        }

        if (!CorePlugin.getInstance().getUuidCache().containsValue(uuid)) {
            CorePlugin.getInstance().getUuidCache().put(name, uuid);
        }
    }

    @Subscription(action = "SERVER_DATA_ONLINE")
    public void onServerDataOnline(JsonAppender jsonAppender) {
        final String bootingServerName = jsonAppender.getParam("SERVER");

        if (!CorePlugin.getInstance().getServerManager().existServer(bootingServerName)) {
            final NetworkServer server = new NetworkServer(bootingServerName, NetworkServerType.NOT_DEFINED);

            server.setServerStatus(NetworkServerStatusType.BOOTING);
            server.setWhitelistEnabled(false);
            server.setOnlinePlayers(0);
            server.setMaxPlayerLimit(0);
            server.setTicksPerSecond("&a0.0&7, &a0.0&7, &a0.0");
            server.setServerType(NetworkServerType.NOT_DEFINED);
            server.setLastUpdate(System.currentTimeMillis());

            final ServerRetrieveEvent retrieveEvent = new ServerRetrieveEvent(server);

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getServer().getPluginManager().callEvent(retrieveEvent));
        }

        PlayerUtil.sendTo("&b[S] &a" + bootingServerName + " &3is now online.", "scandium.network.alerts");
    }

    @Subscription(action = "SERVER_DATA_UPDATE")
    public void onServerDataUpdate(JsonAppender jsonAppender) {
        if (jsonAppender == null) {
            return;
        }

        final String splitPlayers = jsonAppender.getParam("SPLITPLAYERS");
        final String serverName = jsonAppender.getParam("SERVER");
        final String serverType = jsonAppender.getParam("SERVER_TYPE");
        final String ticksPerSecond = jsonAppender.getParam("TPS");
        final String ticksPerSecondSimple = jsonAppender.getParam("TPSSIMPLE");

        final int maxPlayerLimit = Integer.parseInt(jsonAppender.getParam("MAXPLAYERS"));
        final int onlinePlayers = Integer.parseInt(jsonAppender.getParam("ONLINEPLAYERS"));

        final boolean whitelistEnabled = Boolean.parseBoolean(jsonAppender.getParam("WHITELIST"));

        if (!CorePlugin.getInstance().getServerManager().existServer(serverName)) {
            final NetworkServer server = new NetworkServer(serverName, NetworkServerType.valueOf(serverType));

            server.setTicksPerSecond(ticksPerSecond);
            server.setMaxPlayerLimit(maxPlayerLimit);
            server.setOnlinePlayers(onlinePlayers);
            server.setWhitelistEnabled(whitelistEnabled);
            server.setTicksPerSecondSimplified(ticksPerSecondSimple);

            final ServerRetrieveEvent retrieveEvent = new ServerRetrieveEvent(server);

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getServer().getPluginManager().callEvent(retrieveEvent));
        }

        final NetworkServer updatedServer = NetworkServer.getByName(serverName);

        updatedServer.update(onlinePlayers, ticksPerSecond, maxPlayerLimit, whitelistEnabled, ticksPerSecondSimple, true, splitPlayers);
        updatedServer.setServerType(NetworkServerType.valueOf(serverType));

        updatedServer.setLastUpdate(System.currentTimeMillis());
    }

    @Subscription(action = "SERVER_DATA_OFFLINE")
    public void onServerDataOffline(JsonAppender jsonAppender) {
        final String offlineServerName = jsonAppender.getParam("SERVER");
        final NetworkServer networkServer = NetworkServer.getByName(offlineServerName);

        if (networkServer != null) {
            CorePlugin.getInstance().getServerManager().removeNetworkServer(NetworkServer.getByName(offlineServerName));
        }

        PlayerUtil.sendTo("&b[S] &a" + offlineServerName + " &cis now offline.", "scandium.network.alerts");
    }

    @Subscription(action = "PLAYER_CONNECT_UPDATE")
    public void onPlayerConnectUpdate(JsonAppender jsonAppender) {
        final String fromConnectServer = jsonAppender.getParam("SERVER");
        final String connectingPlayer = jsonAppender.getParam("PLAYER");

        PlayerUtil.sendToStaff("&b[S] " + connectingPlayer + " &3connected to &b" + fromConnectServer + "&3.");
    }

    @Subscription(action = "PLAYER_SERVER_SWITCH_UPDATE")
    public void onPlayerSwitchServerUpdate(JsonAppender jsonAppender) {
        final String newServer = jsonAppender.getParam("NEW_SERVER");
        final String fromSwitchingServer = jsonAppender.getParam("SERVER");
        final String switchingPlayer = jsonAppender.getParam("PLAYER");

        PlayerUtil.sendToStaff("&b[S] " + switchingPlayer.replace(" ", "") + " &3joined &b" + newServer + "&3 from &b" + fromSwitchingServer + "&3.");
    }

    @Subscription(action = "PLAYER_DISCONNECT_UPDATE")
    public void onPlayerDisconnectServer(JsonAppender jsonAppender) {
        final String server = jsonAppender.getParam("SERVER");
        final String player = jsonAppender.getParam("PLAYER");

        PlayerUtil.sendToStaff("&b[S] " + player + " &3disconnected from &b" + server + "&3.");
    }

    @Subscription(action = "CHAT_CHANNEL_UPDATE")
    public void onChatChannelUpdate(JsonAppender jsonAppender) {
        final ChatChannelType chatChannel = ChatChannelType.valueOf(jsonAppender.getParam("CHANNEL"));

        final String sender = jsonAppender.getParam("PLAYER");
        final String chatMessage = jsonAppender.getParam("MESSAGE");
        final String fromServer = jsonAppender.getParam("SERVER");

        PlayerUtil.sendTo(
                CorePlugin.getInstance().getPlayerManager().formatChatChannel(chatChannel, sender, chatMessage, fromServer),
                chatChannel.getPermission()
        );
    }

    @Subscription(action = "PLAYER_SERVER_UPDATE")
    public void onPlayerServerUpdate(JsonAppender jsonAppender) {
        final StaffUpdateType updateType = StaffUpdateType.valueOf(jsonAppender.getParam("UPDATETYPE"));

        switch (updateType) {
            case FREEZE:
                final String freezePlayer = jsonAppender.getParam("PLAYER");
                final String freezeServer = jsonAppender.getParam("SERVER");
                final String freezeTarget = jsonAppender.getParam("TARGET");

                PlayerUtil.sendToStaff(updateType.getPrefix() + "&3[" + freezeServer + "] &b" + freezePlayer + " &3froze &b" + freezeTarget + "&3.");
                break;
            case UNFREEZE:
                final String unFreezePlayer = jsonAppender.getParam("PLAYER");
                final String unFreezeTarget = jsonAppender.getParam("TARGET");
                final String unFreezeServer = jsonAppender.getParam("SERVER");

                PlayerUtil.sendToStaff(updateType.getPrefix() + "&3[" + unFreezeServer + "] &b" + unFreezePlayer + " &3unfroze &b" + unFreezeTarget + "&3.");
                break;
            case REQUEST:
                final String requestMessage = jsonAppender.getParam("MESSAGE");
                final String requestPlayer = jsonAppender.getParam("PLAYER");
                final String requestServer = jsonAppender.getParam("SERVER");

                PlayerUtil.sendToStaff(updateType.getPrefix() + "&3[" + requestServer + "] &b" + requestPlayer + " &chas requested assistance: &e" + requestMessage + "&c.");
                break;
            case REPORT:
                final String reportMessage = jsonAppender.getParam("MESSAGE");
                final String reportPlayer = jsonAppender.getParam("PLAYER");
                final String reportTarget = jsonAppender.getParam("TARGET");
                final String reportServer = jsonAppender.getParam("SERVER");

                PlayerUtil.sendToStaff(updateType.getPrefix() + "&3[" + reportServer + "] &b" + reportPlayer + " &chas reported &6" + reportTarget + "&c for &e" + reportMessage + "&c.");
                break;
        }
    }

    @Subscription(action = "RANK_CREATE_UPDATE")
    public void onRankCreateUpdate(JsonAppender jsonAppender) {
        final Player rankCreatePlayer = Bukkit.getPlayer(jsonAppender.getParam("PLAYER"));
        final String rankCreateName = jsonAppender.getParam("NAME");
        final UUID rankCreateUuid = UUID.fromString(jsonAppender.getParam("UUID"));
        final Rank rankCreate = new Rank(rankCreateUuid, rankCreateName);

        if (rankCreatePlayer != null) {
            rankCreatePlayer.sendMessage(Color.SECONDARY_COLOR + "You've created a rank with the name " + ChatColor.GRAY + rankCreateName + Color.SECONDARY_COLOR + "!");
        }

        rankCreate.saveRank();
    }

    @Subscription(action = "RANK_SETTINGS_UPDATE")
    public void onRankSettingsUpdate(JsonAppender jsonAppender) {
        final String rankSettingsServer = jsonAppender.getParam("SERVER");
        final String rankSettingsColor = jsonAppender.getParam("COLOR");
        final String rankSettingsPrefix = jsonAppender.getParam("PREFIX");
        final String rankSettingsSuffix = jsonAppender.getParam("SUFFIX");

        final Rank rankSettingsRank = Rank.getByName(jsonAppender.getParam("RANK"));
        final int rankSettingsWeight = Integer.parseInt(jsonAppender.getParam("WEIGHT"));

        final boolean rankSettingsHidden = Boolean.parseBoolean(jsonAppender.getParam("HIDDEN"));
        final boolean rankSettingsDefault = Boolean.parseBoolean(jsonAppender.getParam("DEFAULT"));
        final boolean rankSettingsPurchasable = Boolean.parseBoolean(jsonAppender.getParam("PURCHASABLE"));
        final boolean rankSettingsItalic = Boolean.parseBoolean(jsonAppender.getParam("ITALIC"));

        final List<String> rankSettingsPermissions = Arrays.asList(jsonAppender.getParam("PERMISSIONS").split(" "));
        /*final List<UUID> rankSettingsInheritance = Arrays.stream(jsonAppender.getParam("INHERITANCE").split(" "))
                .map(UUID::fromString).collect(Collectors.toList());*/

        if (!CorePlugin.getInstance().getServerName().equalsIgnoreCase(rankSettingsServer)) {
            if (rankSettingsRank != null) {
                rankSettingsRank.setColor(rankSettingsColor);
                rankSettingsRank.setPrefix(rankSettingsPrefix);
                rankSettingsRank.setSuffix(rankSettingsSuffix);
                rankSettingsRank.setWeight(rankSettingsWeight);

                rankSettingsRank.setPermissions(rankSettingsPermissions);
//                rankSettingsRank.setInheritance(rankSettingsInheritance);

                rankSettingsRank.setDefaultRank(rankSettingsDefault);
                rankSettingsRank.setHidden(rankSettingsHidden);
                rankSettingsRank.setItalic(rankSettingsItalic);
                rankSettingsRank.setPurchasable(rankSettingsPurchasable);
            }
        }
    }

    @Subscription(action = "RANK_DELETE_UPDATE")
    public void onRankDeleteUpdate(JsonAppender jsonAppender) {
        final Rank rankRemove = Rank.getByName(jsonAppender.getParam("RANK"));

        if (rankRemove != null) {
            final Player rankRemovePlayer = Bukkit.getPlayer(jsonAppender.getParam("PLAYER"));

            if (rankRemovePlayer != null) {
                rankRemovePlayer.sendMessage(Color.SECONDARY_COLOR + "You've deleted the rank with the name " + ChatColor.GRAY + rankRemove.getColor() + rankRemove.getItalic() + rankRemove.getName() + Color.SECONDARY_COLOR + "!");
            }

            CorePlugin.getInstance().getRankManager().getRanks().remove(rankRemove);
        }
    }

    @Subscription(action = "PUNISHMENT_EXECUTE_UPDATE")
    public void onPunishmentExecution(JsonAppender jsonAppender) {
        final String punishmentServer = jsonAppender.getParam("SERVER");

        if (!punishmentServer.equals(CorePlugin.getInstance().getServerName())) {
            final UUID issuerUuid = (jsonAppender.getParam("ISSUER") != null ? UUID.fromString(jsonAppender.getParam("ISSUER")) : null);
            final Punishment punishment = new Punishment(
                    PunishmentType.valueOf(jsonAppender.getParam("TYPE")),
                    issuerUuid,
                    UUID.fromString(jsonAppender.getParam("TARGET")),
                    jsonAppender.getParam("ISSUERNAME"),
                    jsonAppender.getParam("REASON"),
                    new Date(Long.parseLong(jsonAppender.getParam("DATE"))),
                    Long.parseLong(jsonAppender.getParam("DURATION")),
                    Boolean.parseBoolean(jsonAppender.getParam("PERMANENT")),
                    new Date(Long.parseLong(jsonAppender.getParam("CREATED"))),
                    UUID.fromString(jsonAppender.getParam("UUID")),
                    jsonAppender.getParam("IDENTIFICATION"),
                    true
            );

            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(punishment.getTarget());
            final Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(punishment.getTarget()).orElse(null);

            if (potPlayer != null) {
                potPlayer.getPunishments().add(punishment);
                potPlayer.saveWithoutRemove();
            }

            if (document != null) {
                CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, jsonAppender.getParam("ISSUERNAME"), document, Boolean.parseBoolean(jsonAppender.getParam("SILENT")));
            }
        }
    }

    @Subscription(action = "PUNISHMENT_REMOVE_UPDATE")
    public void onPunishmentRemoval(JsonAppender jsonAppender) {
        final String removeServer = jsonAppender.getParam("SERVER");

        if (!removeServer.equals(CorePlugin.getInstance().getServerName())) {
            final Punishment finalPunishment = Punishment.getByIdentification(jsonAppender.getParam("ID"));

            final UUID removerUuid = jsonAppender.getParam("REMOVERUUID") != null ? UUID.fromString(jsonAppender.getParam("REMOVERUUID")) : null;
            final String removerName = jsonAppender.getParam("REMOVERNAME");
            final String removerDisplayName = jsonAppender.getParam("REMOVERDISPLAYNAME");
            final String reason = jsonAppender.getParam("REASON");

            if (finalPunishment != null) {
                finalPunishment.setRemoved(true);
                finalPunishment.setRemovalReason(reason.replace(" -s", ""));
                finalPunishment.setRemover(removerUuid);
                finalPunishment.setActive(false);
                finalPunishment.setRemoverName(removerName);

                final Document punished = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(finalPunishment.getTarget()).orElse(null);

                if (punished != null) {
                    final Rank rank = Rank.getByName(punished.getString("rankName"));
                    final String finalName = (rank != null ? rank.getColor() + rank.getItalic() : ChatColor.GRAY.toString()) + punished.getString("name");

                    if (reason.endsWith("-s")) {
                        PlayerUtil.sendToStaff("&7[Silent] " + finalName + " &awas " + "un" + finalPunishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (removerDisplayName != null ? removerDisplayName : "Console") + ChatColor.GREEN + ".");
                    } else {
                        Bukkit.broadcastMessage(Color.translate(
                                "&7" + finalName + " &awas un" + finalPunishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (removerDisplayName != null ? removerDisplayName : "Console") + ChatColor.GREEN + "."
                        ));
                    }
                }

                finalPunishment.savePunishment();

                final Player targetPlayer = Bukkit.getPlayer(finalPunishment.getTarget());

                if (targetPlayer != null) {
                    final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(targetPlayer);

                    switch (finalPunishment.getPunishmentType()) {
                        case MUTE:
                            targetPlayer.sendMessage(ChatColor.RED + "You've been un-muted by a staff member.");
                            potPlayer.setCurrentlyMuted(false);
                            break;
                        case WARN:
                            targetPlayer.sendMessage(ChatColor.RED + "Your warning has been removed by a staff member.");
                            break;
                        case BLACKLIST:
                            potPlayer.setCurrentlyBlacklisted(false);
                        case IP_BAN:
                            potPlayer.setCurrentlyIpRestricted(false);
                        case BAN:
                            targetPlayer.sendMessage(ChatColor.RED + "You've been unbanned by a staff member.");
                            potPlayer.setCurrentlyRestricted(false);
                            break;
                    }
                }
            }
        }
    }

    @Subscription(action = "PUNISHMENT_F_REMOVE_UPDATE")
    public void onPunishmentForceRemovalUpdate(JsonAppender jsonAppender) {
        final String punishmentRemoveServer = jsonAppender.getParam("SERVER");

        if (!punishmentRemoveServer.equals(CorePlugin.getInstance().getServerName())) {
            final String punishmentString = jsonAppender.getParam("ID");
            final Punishment punishment = Punishment.getByIdentification(punishmentString);

            if (punishment != null) {
                Punishment.getAllPunishments().remove(punishment);
            }
        }
    }

    @Subscription(action = "DISCORD_SYNC_UPDATE")
    public void onDiscordSyncUpdate(JsonAppender jsonAppender) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(jsonAppender.getParam("NAME"));

        if (potPlayer != null) {
            final String discord = jsonAppender.getParam("DISCORD");

            potPlayer.setSynced(true);
            potPlayer.setSyncDiscord(discord);
            potPlayer.getMedia().setDiscord(discord);

            if (!potPlayer.getAllPrefixes().contains("Verified")) {
                potPlayer.getAllPrefixes().add("Verified");
            }

            potPlayer.getPlayer().sendMessage(new String[]{
                    Color.SECONDARY_COLOR + "Thanks for syncing your account! You've been given the " + ChatColor.DARK_GREEN + "✔ " + ChatColor.GRAY + "(Verified) " + Color.SECONDARY_COLOR + "tag!",
                    Color.SECONDARY_COLOR + "Your account has been synced to " + Color.MAIN_COLOR + discord + Color.SECONDARY_COLOR + "."
            });

            RedisUtil.publishAsync(RedisUtil.addGlobalPlayer(potPlayer));
        }
    }

    @Subscription(action = "NETWORK_BROADCAST_UPDATE")
    public void onNetworkBroadcastUpdate(JsonAppender jsonAppender) {
        final String broadcastMessage = jsonAppender.getParam("MESSAGE");

        Bukkit.broadcastMessage(CorePlugin.getInstance().getPlayerManager().formatBroadcast(broadcastMessage));
    }

    @Subscription(action = "NETWORK_BROADCAST_CLICKABLE_UPDATE")
    public void onNetworkBroadcastClickableUpdate(JsonAppender jsonAppender) {
        final Clickable clickable = new Clickable("");

        clickable.add(
                jsonAppender.getParam("TEXT"),
                jsonAppender.getParam("HOVER"),
                jsonAppender.getParam("COMMAND"),
                ClickEvent.Action.RUN_COMMAND
        );

        Bukkit.getOnlinePlayers()
                .forEach(player -> player.spigot().sendMessage(clickable.asComponents()));
    }

    @Subscription(action = "NETWORK_BROADCAST_PERMISSION_UPDATE")
    public void onNetworkBroadcastWithPermissionUpdate(JsonAppender jsonAppender) {
        final String broadcast = jsonAppender.getParam("MESSAGE");
        final String permission = jsonAppender.getParam("PERMISSION");

        PlayerUtil.sendTo(broadcast, permission);
    }
}
