package vip.potclub.core.manager;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

import java.util.List;

@NoArgsConstructor
public class PlayerManager {

    public final List<String> freezeMessage = CorePlugin.getInstance().getConfig().getStringList("freeze-message");

    public void vanishPlayer(Player player) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p != player) {
                if (!p.hasPermission("scandium.vanished.see")) p.hidePlayer(player);
            }
        });
        player.sendMessage(Color.translate("&aYou are now vanished to all online players."));
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().add(player);
    }

    public void unVanishPlayer(Player player) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p != player) p.showPlayer(player);
        });

        player.sendMessage(Color.translate("&aYou are now visible to all online players."));
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().remove(player);
    }

    public String formatChatChannel(ChatChannelType chatChannel, String player, String message, String fromServer) {
        return Color.translate(chatChannel.getPrefix() + "&7[" + fromServer + "] " + player + "&f: &b" + message);
    }

    public void sendDisconnectFreezeMessage(Player target) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            CorePlugin.getInstance().getRedisClient().write(RedisUtil.onGlobalBroadcastPermission(Color.translate("  "), "scandium.staff"));
            CorePlugin.getInstance().getRedisClient().write(RedisUtil.onGlobalBroadcastPermission(Color.translate("&c" + target.getDisplayName() + "&c disconnected while being frozen!"), "scandium.staff"));
            CorePlugin.getInstance().getRedisClient().write(RedisUtil.onGlobalBroadcastPermission(Color.translate("  "), "scandium.staff"));
        });
    }

    public void sendFreezeMessage(Player player) {
        freezeMessage.forEach(s -> player.sendMessage(Color.translate(s)));
    }

    public String formatBroadcast(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }
}
