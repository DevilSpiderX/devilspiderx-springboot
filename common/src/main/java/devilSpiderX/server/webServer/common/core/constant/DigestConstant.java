package devilSpiderX.server.webServer.common.core.constant;


import devilSpiderX.server.webServer.common.core.util.DigestUtils;

/**
 * @author DevilSpiderX
 */
public class DigestConstant {
    public static final String key = "DevilSpiderX";
    public static final String passwordSalt = DigestUtils.sha256(key);
    public static final int minPasswordLength = 8;
}
