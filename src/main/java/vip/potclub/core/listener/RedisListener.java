package vip.potclub.core.listener;

import com.mongodb.client.model.Filters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.enums.NetworkServerStatusType;
import vip.potclub.core.enums.NetworkServerType;
import vip.potclub.core.enums.StaffUpdateType;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.redis.RedisMessage;
import vip.potclub.core.server.NetworkServer;
import vip.potclub.core.util.Color;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class RedisListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        CorePlugin.getInstance().getRedisSubThread().execute(() -> {
            RedisMessage redisMessage = CorePlugin.GSON.fromJson(message, RedisMessage.class);
            switch (redisMessage.getPacket()) {
                case SERVER_DATA_ONLINE:
                    String bootingServerName = redisMessage.getParam("SERVER");

                    if (!CorePlugin.getInstance().getServerManager().existServer(bootingServerName)){
                        NetworkServer server = new NetworkServer(bootingServerName, NetworkServerType.NOT_DEFINED);

                        server.setServerStatus(NetworkServerStatusType.BOOTING);
                        server.setWhitelistEnabled(false);
                        server.setOnlinePlayers(0);
                        server.setMaxPlayerLimit(0);
                        server.setTicksPerSecond("&a0.0&7, &a0.0&7, &a0.0");
                        server.setServerType(NetworkServerType.NOT_DEFINED);
                    }

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (player.hasPermission("scandium.network.alerts")) {
                            player.sendMessage(Color.translate("&3[S] &e" + bootingServerName + " &bhas just booted and will be joinable in &65 seconds&b."));
                        }
                    });
                    break;
                case SERVER_DATA_UPDATE:
                    String serverName = redisMessage.getParam("SERVER");
                    String serverType = redisMessage.getParam("SERVER_TYPE");
                    String ticksPerSecond = redisMessage.getParam("TPS");
                    String ticksPerSecondSimple = redisMessage.getParam("TPSSIMPLE");

                    int maxPlayerLimit = Integer.parseInt(redisMessage.getParam("MAXPLAYERS"));
                    int onlinePlayers = Integer.parseInt(redisMessage.getParam("ONLINEPLAYERS"));

                    boolean whitelistEnabled = Boolean.parseBoolean(redisMessage.getParam("WHITELIST"));

                    if (!CorePlugin.getInstance().getServerManager().existServer(serverName)){
                        NetworkServer server = new NetworkServer(serverName, NetworkServerType.valueOf(serverType));

                        server.setTicksPerSecond(ticksPerSecond);
                        server.setMaxPlayerLimit(maxPlayerLimit);
                        server.setOnlinePlayers(onlinePlayers);
                        server.setWhitelistEnabled(whitelistEnabled);
                        server.setTicksPerSecondSimplified(ticksPerSecondSimple);

                    }
                    NetworkServer.getByName(serverName).update(onlinePlayers, ticksPerSecond, maxPlayerLimit, whitelistEnabled, ticksPerSecondSimple, true);
                    NetworkServer.getByName(serverName).setServerType(NetworkServerType.valueOf(serverType));

                    break;
                case SERVER_DATA_OFFLINE:
                    String offlineServerName = redisMessage.getParam("SERVER");

                    if (NetworkServer.getByName(offlineServerName) != null) {
                        NetworkServer.getByName(offlineServerName).update(0, "0.0", 100, false, "0.0", false);
                        CorePlugin.getInstance().getServerManager().removeNetworkServer(NetworkServer.getByName(offlineServerName));
                    }

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (player.hasPermission("scandium.network.alerts")) {
                            player.sendMessage(Color.translate("&3[S] &e" + offlineServerName + " &bjust went &coffline&b."));
                        }
                    });
                    break;
                case PLAYER_CONNECT_UPDATE:
                    String fromConnectServer = redisMessage.getParam("SERVER");
                    String connectingPlayer = redisMessage.getParam("PLAYER");

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        PotPlayer potPlayer = PotPlayer.getPlayer(player);
                        if (player.hasPermission("scandium.staff")) {
                            if (potPlayer.isCanSeeStaffMessages()) {
                                player.sendMessage(Color.translate("&3[S] " + connectingPlayer + " &bconnected to &3" + fromConnectServer + "&b."));
                            }
                        }
                    });
                    break;
                case PLAYER_DISCONNECT_UPDATE:
                    String fromDisconnectServer = redisMessage.getParam("SERVER");
                    String disconnectingPlayer = redisMessage.getParam("PLAYER");

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        PotPlayer potPlayer = PotPlayer.getPlayer(player);
                        if (player.hasPermission("scandium.staff")) {
                            if (potPlayer.isCanSeeStaffMessages()) {
                                player.sendMessage(Color.translate("&3[S] " + disconnectingPlayer + " &bdisconnected from &3" + fromDisconnectServer + "&b."));
                            }
                        }
                    });
                    break;
                case CHAT_CHANNEL_UPDATE:
                    ChatChannelType chatChannel = ChatChannelType.valueOf(redisMessage.getParam("CHANNEL"));
                    String sender = redisMessage.getParam("PLAYER");
                    String chatMessage = redisMessage.getParam("MESSAGE");
                    String fromServer = redisMessage.getParam("SERVER");

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        PotPlayer potPlayer = PotPlayer.getPlayer(player);
                        if (player.hasPermission(chatChannel.getPermission())) {
                            if (potPlayer.isCanSeeStaffMessages()) {
                                player.sendMessage(CorePlugin.getInstance().getPlayerManager().formatChatChannel(chatChannel, sender, chatMessage, fromServer));
                            }
                        }
                    });
                    break;
                case PLAYER_SERVER_UPDATE:
                    StaffUpdateType updateType = StaffUpdateType.valueOf(redisMessage.getParam("UPDATETYPE"));
                    switch (updateType) {
                        case FREEZE:
                            String freezeExecutor = redisMessage.getParam("PLAYER");
                            String fromFreezeServer = redisMessage.getParam("SERVER");
                            String freezeTarget = redisMessage.getParam("TARGET");

                            Bukkit.getOnlinePlayers().forEach(player -> {
                                PotPlayer potPlayer = PotPlayer.getPlayer(player);
                                if (player.hasPermission(updateType.getPermission())) {
                                    if (potPlayer.isCanSeeStaffMessages()) {
                                        player.sendMessage(Color.translate(updateType.getPrefix() + "&7[" + fromFreezeServer + "] " + "&3" + freezeExecutor + " &bhas frozen &3" + freezeTarget + "&b."));
                                    }
                                }
                            });
                            break;
                        case UNFREEZE:
                            String unfreezeExecutor = redisMessage.getParam("PLAYER");
                            String unfreezeTarget = redisMessage.getParam("TARGET");
                            String fromUnFreezeServer = redisMessage.getParam("SERVER");

                            Bukkit.getOnlinePlayers().forEach(player -> {
                                PotPlayer potPlayer = PotPlayer.getPlayer(player);
                                if (player.hasPermission(updateType.getPermission())) {
                                    if (potPlayer.isCanSeeStaffMessages()) {
                                        player.sendMessage(Color.translate(updateType.getPrefix() + "&7[" + fromUnFreezeServer + "] " + "&3" + unfreezeExecutor + " &bhas unfrozen &3" + unfreezeTarget + "&b."));
                                    }
                                }
                            });
                            break;
                        case HELPOP:
                            String helpopMessage = redisMessage.getParam("MESSAGE");
                            String helpopPlayer = redisMessage.getParam("PLAYER");
                            String fromHelpOpServer = redisMessage.getParam("SERVER");

                            Bukkit.getOnlinePlayers().forEach(player -> {
                                PotPlayer potPlayer = PotPlayer.getPlayer(player);
                                if (player.hasPermission(updateType.getPermission())) {
                                    if (potPlayer.isCanSeeStaffMessages()) {
                                        player.sendMessage(Color.translate(updateType.getPrefix() + "&7[" + fromHelpOpServer + "] " + "&3" + helpopPlayer + " &bhas requested assistance: &e" + helpopMessage + "&b."));
                                    }
                                }
                            });
                            break;
                        case REPORT:
                            String reportMessage = redisMessage.getParam("MESSAGE");
                            String reportPlayer = redisMessage.getParam("PLAYER");
                            String reportTarget = redisMessage.getParam("TARGET");
                            String fromReportServer = redisMessage.getParam("SERVER");

                            Bukkit.getOnlinePlayers().forEach(player -> {
                                PotPlayer potPlayer = PotPlayer.getPlayer(player);
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
                    String newRankName = redisMessage.getParam("NAME");
                    String newRankId = redisMessage.getParam("UUID");
                    Rank newRank = new Rank(UUID.fromString(newRankId), Collections.singletonList(Objects.requireNonNull(Rank.getDefault()).getUuid()), Collections.singletonList("permission.testing"), newRankName, Color.translate("&7"), Color.translate("&7"), Color.translate("&7"), false, 0);
                    Player newRankPlayer = Bukkit.getPlayer(redisMessage.getParam("PLAYER"));
                    if (newRankPlayer != null) newRankPlayer.sendMessage(ChatColor.GREEN + "Rank named '" + newRank.getName() + "' successfully created.");

                    newRank.saveRank();
                    break;
                case RANK_DELETE_UPDATE:
                    Rank delRank = Rank.getByName(redisMessage.getParam("RANK"));
                    if (delRank != null) {
                        Player delRankPlayer = Bukkit.getPlayer(redisMessage.getParam("PLAYER"));
                        if (delRankPlayer != null) delRankPlayer.sendMessage(ChatColor.RED + "Rank named '" + delRank.getName() + "' successfully deleted.");

                        Rank.getRanks().remove(delRank);
                        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getRankCollection().deleteOne(Filters.eq("_id", delRank.getUuid())));
                    }
                    break;
                case RANK_SETTINGS_UPDATE:
                    if (!CorePlugin.getInstance().getConfig().getString("server-id").equals(redisMessage.getParam("SERVER"))) {
                        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> Rank.getRanks().clear());
                        CorePlugin.getInstance().getRankManager().loadRanks();
                        CorePlugin.getInstance().getLogger().info("[Ranks] Synced all ranks.");
                    }
                    break;
                case NETWORK_BROADCAST_UPDATE:
                    String broadcastMessage = redisMessage.getParam("MESSAGE");
                    Bukkit.broadcastMessage(CorePlugin.getInstance().getPlayerManager().formatBroadcast(broadcastMessage));
                    break;
                case NETWORK_BROADCAST_PERMISSION_UPDATE:
                    String broadcast = redisMessage.getParam("MESSAGE");
                    String permission = redisMessage.getParam("PERMISSION");

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (player.hasPermission(permission)) {
                            if (PotPlayer.getPlayer(player).isCanSeeStaffMessages()) {
                                player.sendMessage(Color.translate(broadcast));
                            }
                        }
                    });

                    break;
                default:
                    CorePlugin.getInstance().getLogger().info("[Redis] I received a message, but it was not acknowledged.");
                    break;
            }
        });
    }
}
