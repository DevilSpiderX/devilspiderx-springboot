package devilSpiderX.server.webServer.core.util;

import cn.dev33.satoken.secure.SaSecureUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyCipher {
    private static final String key = "DevilSpiderX";

    public static String encrypt(String value) {
        if (value == null) {
            throw new NullPointerException("参数值不能为空");
        }
        return SaSecureUtil.aesEncrypt(key, value);
    }

    public static String decrypt(String value) {
        if (value == null) {
            throw new NullPointerException("参数值不能为空");
        }
        return SaSecureUtil.aesDecrypt(key, value);
    }

    public static String bytes2Hex(byte[] value) {
        StringBuilder result = new StringBuilder();
        for (byte b : value) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                result.append(0);
            }
            result.append(hex);
        }
        return result.toString();
    }

    public static byte[] MD5(String value) throws NoSuchAlgorithmException {
        MessageDigest MD5Digest = MessageDigest.getInstance("MD5");
        return MD5Digest.digest(value.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] SHA256(String value) throws NoSuchAlgorithmException {
        MessageDigest SHA256Digest = MessageDigest.getInstance("SHA-256");
        return SHA256Digest.digest(value.getBytes(StandardCharsets.UTF_8));
    }
}
