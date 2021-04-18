package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;

@Getter
public class CryptoManager {

    private static final byte[] SALT = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    private final String secretKey;
    private final int iterationCount = 19;

    public CryptoManager() {
        this.secretKey = CorePlugin.getInstance().getConfig().getString("crypto.key");
    }

    public String encrypt(String plainText) {
        Cipher encryptCipher;
        byte[] out;

        try {
            final KeySpec keySpec = new PBEKeySpec(this.secretKey.toCharArray(), CryptoManager.SALT, this.iterationCount);
            final SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            final AlgorithmParameterSpec paramSpec = new PBEParameterSpec(CryptoManager.SALT, this.iterationCount);

            encryptCipher = Cipher.getInstance(key.getAlgorithm());
            encryptCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

            byte[] in = plainText.getBytes(StandardCharsets.UTF_8);
            out = encryptCipher.doFinal(in);
        } catch (Exception ignored) {
            return "";
        }

        return new String(Base64.getEncoder().encode(out));
    }

    public String decrypt(String encryptedText) {
        Cipher decryptCipher;
        byte[] utf8;

        try {
            final KeySpec keySpec = new PBEKeySpec(this.secretKey.toCharArray(), CryptoManager.SALT, this.iterationCount);
            final SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            final AlgorithmParameterSpec paramSpec = new PBEParameterSpec(CryptoManager.SALT, this.iterationCount);

            decryptCipher = Cipher.getInstance(key.getAlgorithm());
            decryptCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

            byte[] enc = Base64.getDecoder().decode(encryptedText);
            utf8 = decryptCipher.doFinal(enc);
        } catch (Exception ignored) {
            return "";
        }

        return new String(utf8, StandardCharsets.UTF_8);
    }
}
