package vip.potclub.core.listener;

import com.google.gson.Gson;
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
                case DELETE_RANK_PERMISSION_UPDATE:
                    Rank rank;
                    try {
                        rank = Rank.getByUuid(UUID.fromString(redisMessage.getParam("RANK")));
                    } catch (Exception ex) {
                        rank = Rank.getByName(redisMessage.getParam("RANK"));
                        if (rank == null) {
                            throw new IllegalArgumentException("Invalid rank parameter");
                        }
                    }
                    if (rank != null) {
                        /*
                        final String permission2 = payload.get("permission").getAsString();
                        rank.getPermissions().remove(permission2);
                        final Player player2 = Bukkit.getPlayer(payload.get("player").getAsString());
                        if (player2 != null) {
                            player2.sendMessage(ChatColor.GREEN + "Permission '" + permission2 + "' successfully removed from rank named '" + rank.getName() + "'.");
                        }
                        for (final Profile profile2 : Profile.getProfiles()) {
                            if (profile2.getActiveGrant().getRank().getUuid().equals(rank.getUuid())) {
                                profile2.setupAtatchment();
                            }
                        }*/
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
                    CorePlugin.getInstance().getLogger().info("[Redis] We received a message, but no params were registered.");
                    break;
            }
        });
    }
}
