package devilSpiderX.server.webServer.service;

import devilSpiderX.server.webServer.MainApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MyCipher {
    private static final Logger logger = LoggerFactory.getLogger(MyCipher.class);
    private static final byte[] key128;

    static {
        String key = "DevilSpiderX";
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            MainApplication.close();
        }
        key128 = keyBytes;
    }


    public static String encrypt(String value) {
        if (value == null || value.equals("")) {
            return value;
        }
        String resultStr = null;
        try {
            byte[] result = doAES(value.getBytes(StandardCharsets.UTF_8), key128, Cipher.ENCRYPT_MODE);
            resultStr = new String(Base64.getEncoder().encode(result), StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            logger.error(e.getMessage(), e);
        }
        return resultStr;
    }

    public static String decrypt(String value) {
        if (value == null || value.equals("")) {
            return value;
        }
        String resultStr = null;
        try {
            byte[] valueBytes = Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8));
            resultStr = new String(doAES(valueBytes, key128, Cipher.DECRYPT_MODE), StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            logger.error(e.getMessage(), e);
        }
        return resultStr;
    }

    private static byte[] doAES(byte[] value, byte[] keyBytes, int mode) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (keyBytes == null) {
            throw new NullPointerException("Parameter keyStr should not be null.");
        }
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, key);
        return cipher.doFinal(value);
    }
}
