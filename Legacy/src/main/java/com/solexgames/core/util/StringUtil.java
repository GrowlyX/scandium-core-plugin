package com.solexgames.core.util;

import com.cryptomorin.xseries.XSound;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.font.DefaultFontInfo;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

@UtilityClass
public final class StringUtil {

    private final static int CENTER_PX = 154;

    public static String buildMessage(String[] args, int start) {
        return start >= args.length ? "" : String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    public static void sendPrivateMessage(Player sender, Player target, String message) {
        final String toMessage = ChatColor.GRAY + "(To " + target.getDisplayName() + ChatColor.GRAY + ") " + message;
        final String fromMessage = ChatColor.GRAY + "(From " + sender.getDisplayName() + ChatColor.GRAY + ") " + message;

        sender.sendMessage(toMessage);
        target.sendMessage(fromMessage);

        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
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

    public static String getCentered(String message) {
        if ((message == null) || message.equals("")) return "";

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

        return sb.toString() + message;
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
