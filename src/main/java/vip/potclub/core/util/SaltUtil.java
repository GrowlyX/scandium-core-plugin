package vip.potclub.core.util;

import java.util.Random;

public final class SaltUtil {

    private final static String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static String getRandomSaltedString() {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < 7) {
            int index = (int) (rnd.nextFloat() * SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }

        return salt.toString();
    }
}
