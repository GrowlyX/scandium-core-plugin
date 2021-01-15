package vip.potclub.core.manager;

import org.bukkit.entity.Player;
import vip.potclub.core.enums.ChatChannel;
import vip.potclub.core.util.Color;

public class PlayerManager {

    public String formatChatChannel(ChatChannel chatChannel, String player, String message) {
        return Color.translate(chatChannel.getPrefix() + player + "&f: &b" + message);
    }

    public String formatBroadcast(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }

    public String formatFrozen(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }
}
