package vip.potclub.core.listener;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.JedisPubSub;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannel;
import vip.potclub.core.enums.ReportType;
import vip.potclub.core.enums.StaffUpdateType;
import vip.potclub.core.redis.RedisMessage;
import vip.potclub.core.util.Color;

public class RedisListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                RedisMessage redisMessage = new Gson().fromJson(message, RedisMessage.class);
                switch (redisMessage.getPacket()) {
                    case CHAT_CHANNEL_UPDATE:
                        ChatChannel chatChannel = ChatChannel.valueOf(redisMessage.getParams().get("CHANNEL"));
                        String sender = redisMessage.getParams().get("PLAYER");
                        String message = redisMessage.getParams().get("MESSAGE");

                        Bukkit.getOnlinePlayers().forEach(player -> {
                            if (player.hasPermission(chatChannel.getPermission())) {
                                player.sendMessage(CorePlugin.getInstance().getPlayerManager().formatChatChannel(chatChannel, sender, message));
                            }
                        });
                        break;
                    case PLAYER_SERVER_UPDATE:
                        StaffUpdateType updateType = StaffUpdateType.valueOf(redisMessage.getParams().get("UPDATETYPE"));
                        switch (updateType) {
                            case FREEZE:
                                String freezeExecutor = redisMessage.getParams().get("PLAYER");
                                String freezeTarget = redisMessage.getParams().get("TARGET");

                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    if (player.hasPermission(updateType.getPermission())) {
                                        player.sendMessage(Color.translate(updateType.getPrefix() + "&3" + freezeExecutor + " &bhas frozen &3" + freezeTarget + "&b."));
                                    }
                                });
                                break;
                            case UNFREEZE:
                                String unfreezeExecutor = redisMessage.getParams().get("PLAYER");
                                String unfreezeTarget = redisMessage.getParams().get("TARGET");

                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    if (player.hasPermission(updateType.getPermission())) {
                                        player.sendMessage(Color.translate(updateType.getPrefix() + "&3" + unfreezeExecutor + " &bhas unfrozen &3" + unfreezeTarget + "&b."));
                                    }
                                });
                                break;
                            case HELPOP:
                                String helpopMessage = redisMessage.getParams().get("MESSAGE");
                                String helpopPlayer = redisMessage.getParams().get("PLAYER");

                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    if (player.hasPermission(updateType.getPermission())) {
                                        player.sendMessage(Color.translate(updateType.getPrefix() + "&3" + helpopPlayer + " &bhas requested assistance: &3" + helpopMessage + "&b."));
                                    }
                                });
                            case REPORT:
                                ReportType reportType = ReportType.valueOf(redisMessage.getParams().get("MESSAGE"));
                                String reportPlayer = redisMessage.getParams().get("PLAYER");
                                String reportTarget = redisMessage.getParams().get("TARGET");

                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    if (player.hasPermission(updateType.getPermission())) {
                                        player.sendMessage(Color.translate(updateType.getPrefix() + "&3" + reportPlayer + " &bhas reported &3" + reportTarget + "&b for &e" + reportType.getName() + "&b."));
                                    }
                                });
                        }
                        break;
                    case NETWORK_BROADCAST_UPDATE:
                        String broadcastMessage = redisMessage.getParams().get("MESSAGE");
                        Bukkit.broadcastMessage(CorePlugin.getInstance().getPlayerManager().formatBroadcast(broadcastMessage));
                        break;
                    default:
                        CorePlugin.getInstance().getLogger().info("[Redis] We received a message, but no params were registered.");
                        break;
                }
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }
}
