package devilSpiderX.server.webServer.common.core.util;

import cn.dev33.satoken.secure.SaSecureUtil;
import devilSpiderX.server.webServer.common.core.constant.DigestConstant;
import devilSpiderX.server.webServer.common.core.exception.BaseException;
import devilSpiderX.server.webServer.common.core.resp.ResultCode;
import jakarta.annotation.Nonnull;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


/**
 * @author DevilSpiderX
 */
public final class DigestUtils {
    private DigestUtils() {
    }

    public static String aesEncrypt(String value) {
        if (value == null) {
            throw new IllegalArgumentException("参数值不能为空");
        }
        return SaSecureUtil.aesEncrypt(DigestConstant.key, value);
    }

    public static String aesDecrypt(String value) {
        if (value == null) {
            throw new IllegalArgumentException("参数值不能为空");
        }
        return SaSecureUtil.aesDecrypt(DigestConstant.key, value);
    }

    /**
     * sha256加密
     *
     * @param str 指定字符串
     * @return 加密后的字符串
     */
    public static @Nonnull String md5(final String str) {
        try {
            final var _str = (str == null ? "" : str);
            final var messageDigest = MessageDigest.getInstance("MD5");
            return getShaHexString(_str, messageDigest);
        } catch (Exception e) {
            throw new BaseException(ResultCode.DigestError, e, e.getMessage());
        }
    }

    /**
     * sha256加密
     *
     * @param str 指定字符串
     * @return 加密后的字符串
     */
    public static @Nonnull String sha256(final String str) {
        try {
            final var _str = (str == null ? "" : str);
            final var messageDigest = MessageDigest.getInstance("SHA-256");
            return getShaHexString(_str, messageDigest);
        } catch (Exception e) {
            throw new BaseException(ResultCode.DigestError, e, e.getMessage());
        }
    }

    /**
     * sha (Secure Hash Algorithm)加密 公共方法
     *
     * @param str           指定字符串
     * @param messageDigest 消息摘要
     * @return 加密后的字符串
     */
    private static @Nonnull String getShaHexString(
            final @Nonnull String str,
            final @Nonnull MessageDigest messageDigest
    ) {
        messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        final var bytes = messageDigest.digest();
        final var builder = new StringBuilder();
        for (byte aByte : bytes) {
            final var temp = Integer.toHexString(aByte & 0xFF); // 获取无符号整数十六进制字符串
            if (temp.length() == 1) {
                builder.append("0"); // 确保每个字节都用两个字符表示
            }
            builder.append(temp);
        }

        return builder.toString();
    }

    /**
     * 使用sha256加密密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static @Nonnull String encryptPassword(final @Nonnull String password) {
        Assert.notNull(password, "password must not be null");
        return sha256(sha256(password) + DigestConstant.passwordSalt);
    }

    public static boolean verifyPassword(final @Nonnull String password, final String hashedPassword) {
        Assert.notNull(password, "password must not be null");
        final var passwordSha256 = encryptPassword(password);
        return passwordSha256.equals(hashedPassword);
    }

}
