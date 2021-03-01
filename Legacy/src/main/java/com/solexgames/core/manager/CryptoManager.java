package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.Setter;

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
@Setter
public class CryptoManager {

    private static final byte[] SALT = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    private final String secretKey;

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    private int iterationCount = 19;

    public CryptoManager() {
        this.secretKey = CorePlugin.getInstance().getConfig().getString("crypto.key");
    }

    public String encrypt(String plainText) {
        byte[] out;
        try {
            KeySpec keySpec = new PBEKeySpec(this.secretKey.toCharArray(), CryptoManager.SALT, this.iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(CryptoManager.SALT, this.iterationCount);

            this.encryptCipher = Cipher.getInstance(key.getAlgorithm());
            this.encryptCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

            byte[] in = plainText.getBytes(StandardCharsets.UTF_8);
            out = this.encryptCipher.doFinal(in);
        } catch (Exception ignored) {
            return "";
        }
        return new String(Base64.getEncoder().encode(out));
    }

    public String decrypt(String encryptedText) {
        byte[] utf8;
        try {
            KeySpec keySpec = new PBEKeySpec(this.secretKey.toCharArray(), CryptoManager.SALT, this.iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(CryptoManager.SALT, this.iterationCount);

            this.decryptCipher = Cipher.getInstance(key.getAlgorithm());
            this.decryptCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

            byte[] enc = Base64.getDecoder().decode(encryptedText);
            utf8 = this.decryptCipher.doFinal(enc);
        } catch (Exception ignored) {
            return "";
        }
        return new String(utf8, StandardCharsets.UTF_8);
    }
}
