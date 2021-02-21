package vip.potclub.core.manager;

import com.mongodb.client.model.Filters;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public class PlayerManager {

    public final List<String> freezeMessage = CorePlugin.getInstance().getConfig().getStringList("freeze-message");

    public void vanishPlayer(Player player) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .filter(player1 -> !player1.hasPermission("scandium.vanished.see"))
                .forEach(p -> p.hidePlayer(player));

        CorePlugin.getInstance().getNMS().removeExecute(player);

        player.sendMessage(Color.translate("&aYou are now vanished to all online players."));
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().add(player);
    }

    public Document getDocumentByUuid(UUID uuid) {
        return CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", uuid)).first();
    }

    public void unVanishPlayer(Player player) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .forEach(p -> p.showPlayer(player));

        CorePlugin.getInstance().getNMS().addExecute(player);

        player.sendMessage(Color.translate("&aYou are now visible to all online players."));
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().remove(player);
    }

    public String formatChatChannel(ChatChannelType chatChannel, String player, String message, String fromServer) {
        return Color.translate(chatChannel.getPrefix() + "&7[" + fromServer + "] " + player + "&f: &b") + message;
    }

    public void sendDisconnectFreezeMessage(Player target) {
        RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("  "), "scandium.staff"));
        RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("&c&l" + target.getName() + "&c disconnected while being frozen!"), "scandium.staff"));
        RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("  "), "scandium.staff"));
    }

    public void sendFreezeMessage(Player player) {
        freezeMessage.forEach(s -> player.sendMessage(Color.translate(s)));
    }

    public String formatBroadcast(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }
}
