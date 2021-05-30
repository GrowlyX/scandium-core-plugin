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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

@UtilityClass
public final class StringUtil {

    private final String PLAYER_NOT_FOUND = ChatColor.RED + "No player matching " + ChatColor.YELLOW + "{0}" + ChatColor.RED + " is connected to this server.";

    private final static int CENTER_PX = 154;
    private static final Pattern FORMATTING = Pattern.compile("^.*(?<format>(§[0-9a-fklmor])+).*");

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
        if ((message == null) || message.equals("")) {
            return "";
        }

        final String finalMessage = Color.translate(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : finalMessage.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                final DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);

                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        final StringBuilder sb = new StringBuilder();
        final int halvedMessageSize = messagePxSize / 2;
        final int toCompensate = CENTER_PX - halvedMessageSize;
        final int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;

        int compensated = 0;

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

    public static List<String> wordWrap(String s, int lineSize) {
        return wordWrap(s, lineSize, lineSize);
    }

    public static List<String> wordWrap(String s, int firstSegment, int lineSize) {
        String format = getFormat(s);
        if (format == null || !s.startsWith(format)) {
            format = "";
        }

        List<String> words = new ArrayList();
        int numChars = firstSegment;
        int ix = 0;
        int jx = 0;

        while (ix < s.length()) {
            ix = s.indexOf(32, ix + 1);
            if (ix == -1) {
                break;
            }

            String subString = s.substring(jx, ix).trim();
            String f = getFormat(subString);
            int chars = stripFormatting(subString).length() + 1;
            if (chars >= numChars) {
                if (f != null) {
                    format = f;
                }

                if (!subString.isEmpty()) {
                    words.add(withFormat(format, subString));
                    numChars = lineSize;
                    jx = ix + 1;
                }
            }
        }

        words.add(withFormat(format, s.substring(jx).trim()));
        return words;
    }

    public static String stripFormatting(String format) {
        return format != null && !format.trim().isEmpty() ? format.replaceAll("(§|&)[0-9a-fklmor]", "") : "";
    }

    private static String withFormat(String format, String subString) {
        String sf = null;
        if (!subString.startsWith("§")) {
            sf = format + subString;
        } else {
            sf = subString;
        }

        return sf;
    }

    private static String getFormat(String s) {
        Matcher m = FORMATTING.matcher(s);
        String format = null;
        if (m.matches() && m.group("format") != null) {
            format = m.group("format");
        }

        return format;
    }

    public static String join(List<String> list, String separator) {
        String joined = "";

        String s;
        for (Iterator var3 = list.iterator(); var3.hasNext(); joined = joined + s + separator) {
            s = (String) var3.next();
        }

        joined = !list.isEmpty() ? joined.substring(0, joined.length() - separator.length()) : joined;
        return joined;
    }

    public static List<String> prefix(List<String> list, String prefix) {
        List<String> prefixed = new ArrayList(list.size());
        Iterator var3 = list.iterator();

        while (var3.hasNext()) {
            String s = (String) var3.next();
            prefixed.add(prefix + s);
        }

        return prefixed;
    }

    public static String camelcase(String name) {
        if (name != null && !name.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            String[] var2 = name.split("[ _]");
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String part = var2[var4];
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1).toLowerCase());
            }

            return sb.toString();
        } else {
            return "";
        }
    }
}
