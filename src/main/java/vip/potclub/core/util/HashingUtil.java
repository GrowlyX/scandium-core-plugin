package vip.potclub.core.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtil {

    private static final String HASHING_SALT = "6P2m2v9bBrFUPjzuj32Uwu2xpcaSmWdv";

    public static String getSaltedMD5(String input) {
        return getMD5(getMD5("6P2m2v9bBrFUPjzuj32Uwu2xpcaSmWdv") + getMD5(input));
    }

    public static String getMD5(String input) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(input.getBytes());
            BigInteger integer = new BigInteger(1, digest);

            String text;
            for(text = integer.toString(16); text.length() < 32; text = "0" + text) {
            }

            return text;
        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5);
        }
    }
}
