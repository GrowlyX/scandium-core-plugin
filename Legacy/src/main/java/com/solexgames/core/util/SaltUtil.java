package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import lombok.experimental.UtilityClass;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

@UtilityClass
public final class SaltUtil {

    private final static String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    /**
     * Builds a randomly generated string with {@link SaltUtil}'s SALT_CHARS
     *
     * @return a random generated string
     */
    public static String getRandomSaltedString() {
        final StringBuilder saltedString = new StringBuilder();

        while (saltedString.length() < 6) {
            int index = (int) (CorePlugin.RANDOM.nextFloat() * SaltUtil.SALT_CHARS.length());
            saltedString.append(SaltUtil.SALT_CHARS.charAt(index));
        }

        return saltedString.toString();
    }

    /**
     * Builds a randomly generated string with {@link SaltUtil}'s SALT_CHARS
     *
     * @param size a custom size
     * @return a random generated string
     */
    public static String getRandomSaltedString(int size) {
        final StringBuilder saltedString = new StringBuilder();

        while (saltedString.length() < size) {
            int index = (int) (CorePlugin.RANDOM.nextFloat() * SaltUtil.SALT_CHARS.length());
            saltedString.append(SaltUtil.SALT_CHARS.charAt(index));
        }

        return saltedString.toString();
    }
}
