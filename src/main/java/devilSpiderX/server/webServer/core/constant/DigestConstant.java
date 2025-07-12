package devilSpiderX.server.webServer.core.constant;


import devilSpiderX.server.webServer.core.util.DigestUtils;

/**
 * @author DevilSpiderX
 */
public class DigestConstant {
    public static final String key = "DevilSpiderX";
    public static final String passwordSalt = DigestUtils.sha256(key);
    public static final int minPasswordLength = 8;
}
