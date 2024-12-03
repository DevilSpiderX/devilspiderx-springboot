package devilSpiderX.server.webServer.module.user.dto;

/**
 * 注册请求参数
 *
 * @param uid 用户id
 * @param pwd 密码
 */
public record RegisterRequest(String uid, String pwd) {
    /**
     * @return 密码
     */
    public String password() {
        return pwd;
    }
}
