package vip.potclub.core.util;

import org.bukkit.ChatColor;

import java.util.Arrays;

public class StringUtil {

    public static final char SQUARE_CHAR = '■';

    public static String buildMessage(String[] args, int start) {
        return start >= args.length ? "" : ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }

    public static String[] formatPrivateMessage(String from, String to, String message) {
        String toMessage = ChatColor.GRAY + "(To " + to + ChatColor.GRAY + ") " + message;
        String fromMessage = ChatColor.GRAY + "(From " + from + ChatColor.GRAY + ") " + message;
        return new String[]{toMessage, fromMessage};
    }
}
