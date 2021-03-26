package com.solexgames.core.redis.subscription.extend;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.NetworkServerStatusType;
import com.solexgames.core.enums.NetworkServerType;
import com.solexgames.core.enums.StaffUpdateType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.redis.json.JsonAppender;
import com.solexgames.core.redis.subscription.AbstractJedisSubscriber;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.UUIDUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CoreJedisSubscriber extends AbstractJedisSubscriber {

    private final String SERVER_NAME = CorePlugin.getInstance().getServerName();

    public CoreJedisSubscriber() {
        super("Scandium:BUKKIT");
    }

    @Override
    public void onMessage(String channel, String message) {
        JsonAppender jsonAppender = CorePlugin.GSON.fromJson(message, JsonAppender.class);

        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            switch (jsonAppender.getPacket()) {
                case GLOBAL_PLAYER_REMOVE:
                    UUID removingPlayer = UUID.fromString(jsonAppender.getParam("UUID"));
                    String removalServer = jsonAppender.getParam("SERVER");

                    if (!removalServer.equalsIgnoreCase(CorePlugin.getInstance().getServerName())) {
                        CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().remove(removingPlayer);
                    }
                    break;
                case GLOBAL_PLAYER_ADDITION:
                    UUID uuid = UUID.fromString(jsonAppender.getParam("UUID"));
                    String name = jsonAppender.getParam("NAME");
                    Rank rank = Rank.getByName(jsonAppender.getParam("RANK"));
                    String globalServer = jsonAppender.getParam("SERVER");
                    String ipAddress = jsonAppender.getParam("IP_ADDRESS");
                    String syncCode = jsonAppender.getParam("SYNC_CODE");
                    boolean dmsEnabled = Boolean.parseBoolean(jsonAppender.getParam("DMS_ENABLED"));
                    boolean isSynced = Boolean.parseBoolean(jsonAppender.getParam("IS_SYNCED"));

                    if (!globalServer.equalsIgnoreCase(CorePlugin.getInstance().getServerName())) {
                        new NetworkPlayer(uuid, name, rank.getName(), globalServer, dmsEnabled, ipAddress, syncCode, isSynced);
                    }
                    break;
                case SERVER_DATA_ONLINE:
                    String bootingServerName = jsonAppender.getParam("SERVER");

                    if (!CorePlugin.getInstance().getServerManager().existServer(bootingServerName)) {
                        NetworkServer server = new NetworkServer(bootingServerName, NetworkServerType.NOT_DEFINED);

                        server.setServerStatus(NetworkServerStatusType.BOOTING);
                        server.setWhitelistEnabled(false);
                        server.setOnlinePlayers(0);
                        server.setMaxPlayerLimit(0);
                        server.setTicksPerSecond("&a0.0&7, &a0.0&7, &a0.0");
                        server.setServerType(NetworkServerType.NOT_DEFINED);
                    }

                    Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("scandium.network.alerts")).forEach(player -> player.sendMessage(Color.translate("&3[S] &e" + bootingServerName + " &bhas just &6booted&b and will be joinable in 5 seconds.")));
                    break;
                case SERVER_DATA_UPDATE:
                    String splitPlayers = jsonAppender.getParam("SPLITPLAYERS");
                    String serverName = jsonAppender.getParam("SERVER");
                    String serverType = jsonAppender.getParam("SERVER_TYPE");
                    String ticksPerSecond = jsonAppender.getParam("TPS");
                    String ticksPerSecondSimple = jsonAppender.getParam("TPSSIMPLE");

                    int maxPlayerLimit = Integer.parseInt(jsonAppender.getParam("MAXPLAYERS"));
                    int onlinePlayers = Integer.parseInt(jsonAppender.getParam("ONLINEPLAYERS"));

                    boolean whitelistEnabled = Boolean.parseBoolean(jsonAppender.getParam("WHITELIST"));

                    if (!CorePlugin.getInstance().getServerManager().existServer(serverName)) {
                        NetworkServer server = new NetworkServer(serverName, NetworkServerType.valueOf(serverType));

                        server.setTicksPerSecond(ticksPerSecond);
                        server.setMaxPlayerLimit(maxPlayerLimit);
                        server.setOnlinePlayers(onlinePlayers);
                        server.setWhitelistEnabled(whitelistEnabled);
                        server.setTicksPerSecondSimplified(ticksPerSecondSimple);

                    }
                    NetworkServer.getByName(serverName).update(onlinePlayers, ticksPerSecond, maxPlayerLimit, whitelistEnabled, ticksPerSecondSimple, true, splitPlayers);
                    NetworkServer.getByName(serverName).setServerType(NetworkServerType.valueOf(serverType));

                    break;
                case SERVER_DATA_OFFLINE:
                    String offlineServerName = jsonAppender.getParam("SERVER");

                    if (NetworkServer.getByName(offlineServerName) != null) {
                        NetworkServer.getByName(offlineServerName).update(0, "0.0", 100, false, "0.0", false, " ");
                        CorePlugin.getInstance().getServerManager().removeNetworkServer(NetworkServer.getByName(offlineServerName));
                    }

                    Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("scandium.network.alerts")).forEach(player -> player.sendMessage(Color.translate("&3[S] &e" + offlineServerName + " &bhas just went &coffline&b and is no longer joinable.")));
                    break;
                case PLAYER_CONNECT_UPDATE:
                    String fromConnectServer = jsonAppender.getParam("SERVER");
                    String connectingPlayer = jsonAppender.getParam("PLAYER");

                    Bukkit.getOnlinePlayers().stream()
                            .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                            .filter(Objects::nonNull)
                            .filter(potPlayer -> potPlayer.getPlayer().hasPermission("scandium.staff"))
                            .filter(PotPlayer::isCanSeeStaffMessages)
                            .forEach(potPlayer -> {
                                potPlayer.getPlayer().sendMessage(Color.translate("&3[S] " + connectingPlayer + " &bconnected to &3" + fromConnectServer + "&b."));

                                if (CorePlugin.getInstance().getLunar() != null) {
                                    CorePlugin.getInstance().getLunar().sendNotification(potPlayer.getPlayer(), Color.translate("&3[S] " + connectingPlayer + " &bconnected to &3" + fromConnectServer + "&b."));
                                }
                            });
                    break;
                case PLAYER_DISCONNECT_UPDATE:
                    String fromDisconnectServer = jsonAppender.getParam("SERVER");
                    String disconnectingPlayer = jsonAppender.getParam("PLAYER");

                    Bukkit.getOnlinePlayers().stream()
                            .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                            .filter(Objects::nonNull)
                            .filter(potPlayer -> potPlayer.getPlayer().hasPermission("scandium.staff"))
                            .filter(PotPlayer::isCanSeeStaffMessages)
                            .forEach(potPlayer -> {
                                potPlayer.getPlayer().sendMessage(Color.translate("&3[S] " + disconnectingPlayer + " &bdisconnected from &3" + fromDisconnectServer + "&b."));

                                if (CorePlugin.getInstance().getLunar() != null) {
                                    CorePlugin.getInstance().getLunar().sendNotification(potPlayer.getPlayer(), Color.translate("&3[S] " + disconnectingPlayer + " &bdisconnected from &3" + fromDisconnectServer + "&b."));
                                }
                            });
                    break;
                case PLAYER_SERVER_SWITCH_UPDATE:
                    String newServer = jsonAppender.getParam("NEW_SERVER");
                    String fromSwitchingServer = jsonAppender.getParam("SERVER");
                    String switchingPlayer = jsonAppender.getParam("PLAYER");

                    Bukkit.getOnlinePlayers().stream()
                            .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                            .filter(Objects::nonNull)
                            .filter(potPlayer -> potPlayer.getPlayer().hasPermission("scandium.staff"))
                            .filter(PotPlayer::isCanSeeStaffMessages)
                            .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate((!fromSwitchingServer.equalsIgnoreCase(newServer)) ? "&3[S] " + switchingPlayer + " &bjoined &3" + newServer + "&b from &3" + fromSwitchingServer + "&b." : "&3[S] " + switchingPlayer + " &bconnected to &3" + newServer + "&b.")));
                    break;
                case CHAT_CHANNEL_UPDATE:
                    ChatChannelType chatChannel = ChatChannelType.valueOf(jsonAppender.getParam("CHANNEL"));
                    String sender = jsonAppender.getParam("PLAYER");
                    String chatMessage = jsonAppender.getParam("MESSAGE");
                    String fromServer = jsonAppender.getParam("SERVER");

                    Bukkit.getOnlinePlayers().stream()
                            .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                            .filter(Objects::nonNull)
                            .filter(potPlayer -> potPlayer.getPlayer().hasPermission("scandium.staff"))
                            .filter(PotPlayer::isCanSeeStaffMessages)
                            .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(CorePlugin.getInstance().getPlayerManager().formatChatChannel(chatChannel, sender, chatMessage, fromServer)));
                    break;
                case PLAYER_SERVER_UPDATE:
                    StaffUpdateType updateType = StaffUpdateType.valueOf(jsonAppender.getParam("UPDATETYPE"));
                    switch (updateType) {
                        case FREEZE:
                            String freezeExecutor = jsonAppender.getParam("PLAYER");
                            String fromFreezeServer = jsonAppender.getParam("SERVER");
                            String freezeTarget = jsonAppender.getParam("TARGET");

                            Bukkit.getOnlinePlayers().stream()
                                    .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                                    .filter(Objects::nonNull)
                                    .filter(potPlayer -> potPlayer.getPlayer().hasPermission(updateType.getPermission()))
                                    .filter(PotPlayer::isCanSeeStaffMessages)
                                    .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate(updateType.getPrefix() + "&7[" + fromFreezeServer + "] " + "&3" + freezeExecutor + " &bhas frozen &3" + freezeTarget + "&b.")));
                            break;
                        case UNFREEZE:
                            String unfreezeExecutor = jsonAppender.getParam("PLAYER");
                            String unfreezeTarget = jsonAppender.getParam("TARGET");
                            String fromUnFreezeServer = jsonAppender.getParam("SERVER");

                            Bukkit.getOnlinePlayers().stream()
                                    .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                                    .filter(Objects::nonNull)
                                    .filter(potPlayer -> potPlayer.getPlayer().hasPermission(updateType.getPermission()))
                                    .filter(PotPlayer::isCanSeeStaffMessages)
                                    .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate(updateType.getPrefix() + "&7[" + fromUnFreezeServer + "] " + "&3" + unfreezeExecutor + " &bhas unfrozen &3" + unfreezeTarget + "&b.")));
                            break;
                        case HELPOP:
                            String helpOpMessage = jsonAppender.getParam("MESSAGE");
                            String helpOpPlayer = jsonAppender.getParam("PLAYER");
                            String fromHelpOpServer = jsonAppender.getParam("SERVER");

                            Bukkit.getOnlinePlayers().forEach(player -> {
                                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                                if (player.hasPermission(updateType.getPermission())) {
                                    if (potPlayer.isCanSeeStaffMessages()) {
                                        player.sendMessage(Color.translate(updateType.getPrefix() + "&7[" + fromHelpOpServer + "] " + "&3" + helpOpPlayer + " &bhas requested assistance: &e" + helpOpMessage + "&b."));
                                    }
                                }
                            });
                            break;
                        case REPORT:
                            String reportMessage = jsonAppender.getParam("MESSAGE");
                            String reportPlayer = jsonAppender.getParam("PLAYER");
                            String reportTarget = jsonAppender.getParam("TARGET");
                            String fromReportServer = jsonAppender.getParam("SERVER");

                            Bukkit.getOnlinePlayers().forEach(player -> {
                                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                                if (player.hasPermission(updateType.getPermission())) {
                                    if (potPlayer.isCanSeeStaffMessages()) {
                                        player.sendMessage(Color.translate(updateType.getPrefix() + "&7[" + fromReportServer + "] " + "&3" + reportPlayer + " &bhas reported &3" + reportTarget + "&b for &e" + reportMessage + "&b."));
                                    }
                                }
                            });
                            break;
                    }
                    break;
                case RANK_CREATE_UPDATE:
                    String newRankName = jsonAppender.getParam("NAME");
                    String newRankId = jsonAppender.getParam("UUID");
                    Rank newRank = new Rank(UUID.fromString(newRankId), Collections.singletonList(Objects.requireNonNull(Rank.getDefault()).getUuid()), Collections.singletonList("permission.testing"), newRankName, Color.translate("&7"), Color.translate("&7"), Color.translate("&7"), false, 0);
                    Player newRankPlayer = Bukkit.getPlayer(jsonAppender.getParam("PLAYER"));
                    if (newRankPlayer != null)
                        newRankPlayer.sendMessage(ChatColor.GREEN + "Rank named '" + newRank.getName() + "' successfully created.");

                    newRank.saveRank();
                    break;
                case RANK_DELETE_UPDATE:
                    Rank delRank = Rank.getByName(jsonAppender.getParam("RANK"));
                    if (delRank != null) {
                        Player delRankPlayer = Bukkit.getPlayer(jsonAppender.getParam("PLAYER"));
                        if (delRankPlayer != null)
                            delRankPlayer.sendMessage(ChatColor.RED + "Rank named '" + delRank.getName() + "' successfully deleted.");

                        Rank.getRanks().remove(delRank);
                        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getRankCollection().deleteOne(Filters.eq("_id", delRank.getUuid())));
                    }
                    break;
                case PUNISHMENT_EXECUTE_UPDATE:
                    if (!SERVER_NAME.equals(jsonAppender.getParam("SERVER"))) {
                        UUID uuid1 = null;

                        if (jsonAppender.getParam("ISSUER") != null) {
                            uuid1 = UUID.fromString(jsonAppender.getParam("ISSUER"));
                        }

                        Punishment punishment = new Punishment(PunishmentType.valueOf(jsonAppender.getParam("TYPE")), uuid1, UUID.fromString(jsonAppender.getParam("TARGET")), jsonAppender.getParam("ISSUERNAME"), jsonAppender.getParam("REASON"), new Date(Long.parseLong(jsonAppender.getParam("DATE"))), Long.parseLong(jsonAppender.getParam("DURATION")), Boolean.parseBoolean(jsonAppender.getParam("PERMANENT")), new Date(Long.parseLong(jsonAppender.getParam("CREATED"))), UUID.fromString(jsonAppender.getParam("UUID")), jsonAppender.getParam("IDENTIFICATION"), true);
                        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(punishment.getIssuerName());

                        if (potPlayer != null) {
                            potPlayer.getPunishments().add(punishment);
                            potPlayer.saveWithoutRemove();
                        }

                        Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(UUID.fromString(jsonAppender.getParam("ISSUER"))).orElse(null);
                        CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, jsonAppender.getParam("ISSUERNAME"), document, Boolean.parseBoolean(jsonAppender.getParam("SILENT")));
                    }
                    break;
                case PUNISHMENT_REMOVE_UPDATE:
                    if (!SERVER_NAME.equals(jsonAppender.getParam("SERVER"))) {
                        Punishment punishment = null;

                        UUID removerUuid = UUID.fromString(jsonAppender.getParam("REMOVERUUID"));
                        String removerName = jsonAppender.getParam("REMOVERNAME");
                        String removerDisplayName = jsonAppender.getParam("REMOVERDISPLAYNAME");
                        String reason = jsonAppender.getParam("REASON");

                        try {
                            punishment = Punishment.getByIdentification(jsonAppender.getParam("ID"));
                        } catch (Exception ignored) {
                        }

                        Punishment finalPunishment = punishment;

                        if (finalPunishment != null) {
                            finalPunishment.setRemoved(true);
                            finalPunishment.setRemovalReason(reason.replace("-s", ""));
                            finalPunishment.setRemover(removerUuid);
                            finalPunishment.setActive(false);
                            finalPunishment.setRemoverName(removerName);

                            String punishedName = UUIDUtil.fetchName(punishment.getTarget());

                            if (reason.endsWith("-s")) {
                                Bukkit.getOnlinePlayers()
                                        .stream()
                                        .filter(player -> player.hasPermission("scandium.staff"))
                                        .forEach(player1 -> player1.sendMessage(Color.translate(
                                                "&7[S] " + punishedName + " &awas " + "un" + finalPunishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (removerDisplayName != null ? removerDisplayName : "Console") + "&a."
                                        )));
                            } else {
                                Bukkit.broadcastMessage(Color.translate(
                                        "&7" + punishedName + " &awas un" + finalPunishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (removerDisplayName != null ? removerDisplayName : "Console") + "&a."
                                ));
                            }

                            finalPunishment.savePunishment();

                            Player targetPlayer = Bukkit.getPlayer(finalPunishment.getTarget());

                            if (targetPlayer != null) {
                                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(targetPlayer);

                                switch (punishment.getPunishmentType()) {
                                    case MUTE:
                                        potPlayer.setCurrentlyMuted(false);
                                        break;
                                    case WARN:
                                        targetPlayer.sendMessage(ChatColor.RED + "Your current warning has now expired.");
                                        break;
                                    case BLACKLIST:
                                    case IPBAN:
                                    case BAN:
                                        potPlayer.setCurrentlyRestricted(false);
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case RANK_SETTINGS_UPDATE:
                    String rankSettingsServer = jsonAppender.getParam("SERVER");
                    String rankSettingsColor = jsonAppender.getParam("COLOR");
                    String rankSettingsPrefix = jsonAppender.getParam("PREFIX");
                    String rankSettingsSuffix = jsonAppender.getParam("SUFFIX");

                    Rank rankSettingsRank = Rank.getByName(jsonAppender.getParam("RANK"));
                    int rankSettingsWeight = Integer.parseInt(jsonAppender.getParam("WEIGHT"));

                    boolean rankSettingsHidden = Boolean.parseBoolean(jsonAppender.getParam("HIDDEN"));
                    boolean rankSettingsDefault = Boolean.parseBoolean(jsonAppender.getParam("DEFAULT"));

                    List<String> rankSettingsPermissions = Arrays.asList(jsonAppender.getParam("PERMISSIONS").split(" "));
                    List<UUID> rankSettingsInheritance = Arrays.stream(jsonAppender.getParam("INHERITANCE").split(" ")).map(UUID::fromString).collect(Collectors.toList());

                    if (!SERVER_NAME.equalsIgnoreCase(rankSettingsServer)) {
                        if (rankSettingsRank != null) {
                            rankSettingsRank.setColor(rankSettingsColor);
                            rankSettingsRank.setPrefix(rankSettingsPrefix);
                            rankSettingsRank.setSuffix(rankSettingsSuffix);
                            rankSettingsRank.setWeight(rankSettingsWeight);

                            rankSettingsRank.setPermissions(rankSettingsPermissions);
                            rankSettingsRank.setInheritance(rankSettingsInheritance);

                            rankSettingsRank.setDefaultRank(rankSettingsDefault);
                            rankSettingsRank.setHidden(rankSettingsHidden);
                        }
                    }

                    break;
                case PUNISHMENT_F_REMOVE_UPDATE:
                    if (!SERVER_NAME.equalsIgnoreCase(jsonAppender.getParam("SERVER"))) {
                        String punishmentString = jsonAppender.getParam("ID");
                        Punishment punishment = Punishment.getByIdentification(punishmentString);
                        if (punishment != null) {
                            Punishment.getAllPunishments().remove(punishment);
                        }
                    }
                    break;
                case DISCORD_SYNC_UPDATE:
                    PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(jsonAppender.getParam("NAME"));

                    if (potPlayer != null) {
                        String discord = jsonAppender.getParam("DISCORD");

                        potPlayer.setSynced(true);
                        potPlayer.setSyncDiscord(discord);
                        potPlayer.getMedia().setDiscord(discord);
                        potPlayer.getAllPrefixes().add("Verified");

                        potPlayer.getPlayer().sendMessage(new String[] {
                                "  ",
                                Color.translate("&aThanks for syncing your account! You have been given the &2✔ &7(Verified) &atag!"),
                                Color.translate("&aYour account has been synced to &b" + discord + "&a."),
                                "  "
                        });
                    }
                    break;
                case NETWORK_BROADCAST_UPDATE:
                    String broadcastMessage = jsonAppender.getParam("MESSAGE");
                    Bukkit.broadcastMessage(CorePlugin.getInstance().getPlayerManager().formatBroadcast(broadcastMessage));
                    break;
                case NETWORK_BROADCAST_PERMISSION_UPDATE:
                    String broadcast = jsonAppender.getParam("MESSAGE");
                    String permission = jsonAppender.getParam("PERMISSION");

                    Bukkit.getOnlinePlayers().stream()
                            .filter(player -> player.hasPermission(permission))
                            .forEach(player -> player.sendMessage(Color.translate(broadcast)));
                    break;
                default:
                    break;
            }
        });
    }
}
