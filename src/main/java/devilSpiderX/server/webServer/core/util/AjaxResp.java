package devilSpiderX.server.webServer.core.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class AjaxResp<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7635332772845242522L;

    public static final int CODE_SUCCESS = 0;           // 成功状态码
    public static final int CODE_FAILURE = 1;           // 失败状态码
    public static final int CODE_ERROR = 1000;           // 错误状态码
    public static final int CODE_WARNING = 1001;         // 警告状态码
    public static final int CODE_NOT_LOGIN = 1002;          // 未登录状态码
    public static final int CODE_NOT_ROLE = 1003;           // 非指定角色状态码
    public static final int CODE_NOT_PERMISSION = 1004;     // 非指定权限状态码

    private static final AjaxResp<Void> SUCCESS_RESP = new AjaxResp<>(CODE_SUCCESS, "OK");
    private static final AjaxResp<Void> FAILURE_RESP = new AjaxResp<>(CODE_FAILURE, "Failure");
    private static final AjaxResp<Void> ERROR_RESP = new AjaxResp<>(CODE_ERROR, "Error");
    private static final AjaxResp<Void> WARNING_RESP = new AjaxResp<>(CODE_WARNING, "Warning");

    private final Integer code;
    private final String msg;
    private T data;
    private Long dataCount;

    public AjaxResp(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public AjaxResp(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.dataCount = null;
    }

    public AjaxResp(int code, String msg, T data, Long dataCount) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.dataCount = dataCount;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public AjaxResp<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public AjaxResp<T> setDataCount(Long dataCount) {
        this.dataCount = dataCount;
        return this;
    }

    @Override
    public String toString() {
        return JacksonUtil.toJSONString(this);
    }

    // 返回成功
    public static AjaxResp<Void> success() {
        return SUCCESS_RESP;
    }

    public static AjaxResp<Void> success(String msg) {
        return new AjaxResp<>(CODE_SUCCESS, msg);
    }

    public static <T> AjaxResp<T> success(T data) {
        return new AjaxResp<>(CODE_SUCCESS, "OK", data);
    }

    public static <T> AjaxResp<T> success(String msg, T data) {
        return new AjaxResp<>(CODE_SUCCESS, msg, data);
    }

    //返回失败
    public static AjaxResp<Void> failure() {
        return FAILURE_RESP;
    }


    public static AjaxResp<Void> failure(String msg) {
        return new AjaxResp<>(CODE_FAILURE, msg);
    }

    // 返回错误
    public static AjaxResp<Void> error() {
        return ERROR_RESP;
    }


    public static <T> AjaxResp<T> error(String msg) {
        return new AjaxResp<>(CODE_ERROR, msg);
    }

    // 返回警告
    public static AjaxResp<Void> warning() {
        return WARNING_RESP;
    }

    public static <T> AjaxResp<T> warning(String msg) {
        return new AjaxResp<>(CODE_WARNING, msg);
    }

    // 返回未登录
    public static AjaxResp<Void> notLogin() {
        return new AjaxResp<>(CODE_NOT_LOGIN, "未登录，请登录后再次访问");
    }

    public static AjaxResp<Void> notLogin(String msg) {
        return new AjaxResp<>(CODE_NOT_LOGIN, msg);
    }

    // 返回无角色权限
    public static AjaxResp<?> notRole(String role) {
        return new AjaxResp<>(CODE_NOT_ROLE, "没有%s角色权限".formatted(role), Map.of("role", role));
    }

    // 返回无权限
    public static AjaxResp<?> notPermission(String permission) {
        return new AjaxResp<>(
                CODE_NOT_PERMISSION,
                "没有%s权限".formatted(permission),
                Map.of("permission", permission)
        );
    }

    // 返回一个自定义状态码的
    public static <T> AjaxResp<T> of(int code, String msg) {
        return new AjaxResp<>(code, msg);
    }

    public static <T> AjaxResp<T> of(int code, String msg, T data) {
        return new AjaxResp<>(code, msg, data);
    }

    public static <T> AjaxResp<T> of(AjaxResp<?> res, T data) {
        return new AjaxResp<>(res.getCode(), res.getMsg(), data);
    }

    public static <T> AjaxResp<T> of(AjaxResp<?> res, String msg, T data) {
        return new AjaxResp<>(res.getCode(), msg, data);
    }
}
