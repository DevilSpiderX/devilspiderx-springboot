package devilSpiderX.server.webServer.module.user.dto;

/**
 * 登录请求参数
 *
 * @param uid 用户id
 * @param pwd 密码
 */
public record LoginRequest(String uid, String pwd) {
    /**
     * @return 密码
     */
    public String password() {
        return pwd;
    }
}
