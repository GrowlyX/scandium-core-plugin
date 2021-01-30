package vip.potclub.core.util;

import vip.potclub.core.CorePlugin;

import java.util.Random;

public final class SaltUtil {

    private final static String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase();

    public static String getRandomSaltedString() {
        StringBuilder salt = new StringBuilder();

        while (salt.length() < 6) {
            int index = (int) (CorePlugin.RANDOM.nextFloat() * SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }

        return salt.toString();
    }
}
