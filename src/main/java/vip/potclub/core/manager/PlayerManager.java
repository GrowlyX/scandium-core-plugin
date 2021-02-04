package vip.potclub.core.manager;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.util.Color;

@NoArgsConstructor
public class PlayerManager {

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

    public String formatBroadcast(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }
}
