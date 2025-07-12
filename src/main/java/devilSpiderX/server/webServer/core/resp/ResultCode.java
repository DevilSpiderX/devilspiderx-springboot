package devilSpiderX.server.webServer.core.resp;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务代码、基础消息枚举类型</br>
 * 主要是为{@link CommonResult}提供枚举支持
 *
 * @author DevilSpiderX
 */
@AllArgsConstructor
@Getter
public enum ResultCode {
    // 默认状态码
    Undefined(-1, "未定义异常"),
    Success(0, "ok"),
    Error(1, "error"),
    JacksonUtilError(2, "json解析出错"),
    IllegalArgument(3, "非法参数"),
    IllegalState(4, "非法状态"),
    DigestError(5, "hash加密报错"),

    // 登录状态码
    NotLogin(900, "未登录"),
    BeReplaced(901, "被顶下线"),
    KickOut(902, "被系统踢下线"),
    TokenFreeze(903, "token已被冻结"),
    NotRole(904, "缺失角色"),
    NotPermission(905, "缺失权限"),

    // 用户状态码
    UserExists(1000, "用户名已存在"),
    UserNotExists(1001, "用户不存在"),
    UserRegisterFailure(1002, "用户注册失败"),
    UpdateLastAddrFailure(1003, "更新最后登录地址失败"),
    UserWrongPassword(1004, "用户密码错误"),
    UpdateUserPasswordFailure(1005, "修改用户密码失败"),
    UploadAvatarFailure(1006, "上传头像失败"),

    // 密码记录状态码
    MyPasswordExists(1100, "密码记录已存在"),
    MyPasswordNotExists(1101, "密码记录不存在"),
    MyPasswordAddFailure(1102, "密码记录添加失败"),
    MyPasswordDeleteFailure(1103, "密码记录删除失败"),
    MyPasswordUpdateFailure(1104, "密码记录更新失败"),

    ;
    /**
     * 业务代码
     */
    private final int code;
    /**
     * 消息
     */
    private final @Nonnull String message;

    public static @Nonnull ResultCode fromCode(int code) {
        for (ResultCode rc : ResultCode.values()) {
            if (rc.getCode() != code) continue;
            return rc;
        }
        return Undefined;
    }
}
