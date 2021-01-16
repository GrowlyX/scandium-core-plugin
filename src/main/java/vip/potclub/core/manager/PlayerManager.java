package vip.potclub.core.manager;

import vip.potclub.core.enums.ChatChannel;
import vip.potclub.core.util.Color;

public class PlayerManager {

    public String formatChatChannel(ChatChannel chatChannel, String player, String message, String fromServer) {
        return Color.translate(chatChannel.getPrefix() + "&7[" + fromServer + "] " + player + "&f: &b" + message);
    }

    public String formatBroadcast(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }
}
