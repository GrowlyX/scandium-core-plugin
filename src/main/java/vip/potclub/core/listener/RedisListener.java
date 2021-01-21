package vip.potclub.core.listener;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.enums.StaffUpdateType;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.redis.RedisMessage;
import vip.potclub.core.util.Color;

public class RedisListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        CorePlugin.getInstance().getRedisSubThread().execute(() -> {
            RedisMessage redisMessage = new Gson().fromJson(message, RedisMessage.class);
            switch (redisMessage.getPacket()) {
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
                case NETWORK_BROADCAST_UPDATE:
                    String broadcastMessage = redisMessage.getParam("MESSAGE");
                    Bukkit.broadcastMessage(CorePlugin.getInstance().getPlayerManager().formatBroadcast(broadcastMessage));
                    break;
                default:
                    CorePlugin.getInstance().getLogger().info("[Redis] We received a message, but no params were registered.");
                    break;
            }
        });
    }
}
