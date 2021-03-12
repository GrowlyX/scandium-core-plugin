package com.solexgames.core.util;

import com.cryptomorin.xseries.XSound;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.font.DefaultFontInfo;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

public final class StringUtil {

    private final static int CENTER_PX = 154;

    public static String buildMessage(String[] args, int start) {
        return start >= args.length ? "" : String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    public static void sendStaffAlert(Player player, String value) {
        if (CorePlugin.getInstance().getConfig().getBoolean("operator-alerts")) {
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisManager().write(RedisUtil.onGlobalBroadcastPermission(Color.translate("&7&o[" + player.getName() + ": &e&o" + value + "&7&o]"), "scandium.operator.alerts")));
        }
    }

    public static void sendPrivateMessage(Player sender, Player target, String message) {
        String toMessage = ChatColor.GRAY + "(To " + target.getDisplayName() + ChatColor.GRAY + ") " + message;
        String fromMessage = ChatColor.GRAY + "(From " + sender.getDisplayName() + ChatColor.GRAY + ") " + message;

        sender.sendMessage(toMessage);
        target.sendMessage(fromMessage);

        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
        if (potPlayer.isCanReceiveDmsSounds()) {
            target.playSound(target.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1.0F, 1.0F);
        }
    }

    public static void sendCenteredMessage(Player player, String message) {
        if ((message == null) || message.equals("")) return;

        message = Color.translate(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;

        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        player.sendMessage(sb.toString() + message.replace("%PLAYER%", player.getDisplayName()));
    }

    public static void sendCenteredMessage(Player player, ArrayList<String> messages) {
        messages.forEach(message -> {
            if ((message == null) || message.equals("")) return;

            message = Color.translate(message);

            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;

            for (char c : message.toCharArray()) {
                if (c == '§') {
                    previousCode = true;
                } else if (previousCode) {
                    previousCode = false;
                    isBold = c == 'l' || c == 'L';
                } else {
                    DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                    messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                    messagePxSize++;
                }
            }

            int halvedMessageSize = messagePxSize / 2;
            int toCompensate = CENTER_PX - halvedMessageSize;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;

            StringBuilder sb = new StringBuilder();
            while (compensated < toCompensate) {
                sb.append(" ");
                compensated += spaceLength;
            }

            player.sendMessage(sb.toString() + message.replace("%PLAYER%", player.getDisplayName()));
        });
    }
}
