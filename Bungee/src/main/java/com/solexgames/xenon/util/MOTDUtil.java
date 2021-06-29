package com.solexgames.xenon.util;

import com.solexgames.xenon.CorePlugin;

public final class MOTDUtil {

    private final static int CENTER_PX = 132;

    public static String getCenteredMotd(String message) {
        if (message == null || message.equals(""))
            return "";
        message = Color.translate(message);

        if (!CorePlugin.getInstance().isCenterAuto()) {
            return message;
        }

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
                FontInformation dFI = FontInformation.getDefaultFontInfo(c);

                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = FontInformation.SPACE.getLength() + 1;
        int compensated = 0;

        StringBuilder sb = new StringBuilder();

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb.toString() + message;
    }
}
