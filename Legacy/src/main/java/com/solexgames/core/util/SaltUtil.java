package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;

public final class SaltUtil {

    private final static String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    public static String getRandomSaltedString() {
        StringBuilder salt = new StringBuilder();

        while (salt.length() < 6) {
            int index = (int) (CorePlugin.RANDOM.nextFloat() * SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }

        return salt.toString();
    }

    public static String getRandomSaltedString(int size) {
        StringBuilder salt = new StringBuilder();

        while (salt.length() < size) {
            int index = (int) (CorePlugin.RANDOM.nextFloat() * SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }

        return salt.toString();
    }
}