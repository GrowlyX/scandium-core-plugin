package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

public final class SaltUtil {

    private final static String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    public static String getRandomSaltedString() {
        final StringBuilder saltedString = new StringBuilder();

        while (saltedString.length() < 6) {
            int index = (int) (CorePlugin.RANDOM.nextFloat() * SaltUtil.SALT_CHARS.length());
            saltedString.append(SaltUtil.SALT_CHARS.charAt(index));
        }

        return saltedString.toString();
    }

    public static String getRandomSaltedString(int size) {
        final StringBuilder saltedString = new StringBuilder();

        while (saltedString.length() < size) {
            int index = (int) (CorePlugin.RANDOM.nextFloat() * SaltUtil.SALT_CHARS.length());
            saltedString.append(SaltUtil.SALT_CHARS.charAt(index));
        }

        return saltedString.toString();
    }
}
