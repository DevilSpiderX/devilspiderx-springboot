package devilSpiderX.server.webServer.core.util;

public class AjaxCode {
    /**
     * 成功
     */
    public static final int SUCCESS = 0;
    /**
     * 失败
     */
    public static final int FAILURE = 1;
    /**
     * 错误
     */
    public static final int ERROR = 1000;
    /**
     * 警告
     */
    public static final int WARNING = 1001;
    /**
     * 未登录
     */
    public static final int NOT_LOGIN = 1002;
    /**
     * 非指定角色
     */
    public static final int NOT_ROLE = 1003;
    /**
     * 非指定权限
     */
    public static final int NOT_PERMISSION = 1004;
    /**
     * 实体所有者和用户不相符
     */
    public static final int ENTITY_OWNER_NOT_MATCH = 1005;
}
