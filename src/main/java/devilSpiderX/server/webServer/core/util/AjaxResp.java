package devilSpiderX.server.webServer.core.util;

import com.alibaba.fastjson2.JSON;

import java.io.Serial;
import java.io.Serializable;

public class AjaxResp<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7635332772845242522L;

    public static final int CODE_SUCCESS = 0;           // 成功状态码
    public static final int CODE_FAILURE = 1;           // 失败状态码
    public static final int CODE_ERROR = 1000;           // 错误状态码
    public static final int CODE_WARNING = 1001;         // 警告状态码
    public static final int CODE_NOT_LOGIN = 1002;           // 未登录状态码
    public static final int CODE_NOT_ADMIN = 1003;           // 无权限状态码

    private final Integer code;
    private String msg;
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

    public AjaxResp<T> setMsg(String msg) {
        this.msg = msg;
        return this;
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
        return JSON.toJSONString(this);
    }

    // 返回成功
    public static AjaxResp<Void> success() {
        return new AjaxResp<>(CODE_SUCCESS, "OK");
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
        return new AjaxResp<>(CODE_FAILURE, "OK");
    }


    public static AjaxResp<Void> failure(String msg) {
        return new AjaxResp<>(CODE_FAILURE, msg);
    }

    // 返回错误
    public static <T> AjaxResp<T> error() {
        return new AjaxResp<>(CODE_ERROR, "Error");
    }


    public static <T> AjaxResp<T> error(String msg) {
        return new AjaxResp<>(CODE_ERROR, msg);
    }

    // 返回警告
    public static <T> AjaxResp<T> warning() {
        return new AjaxResp<>(CODE_ERROR, "Warning");
    }

    public static <T> AjaxResp<T> warning(String msg) {
        return new AjaxResp<>(CODE_WARNING, msg);
    }

    // 返回未登录
    public static AjaxResp<Void> notLogin() {
        return new AjaxResp<>(CODE_NOT_LOGIN, "未登录，请登录后再次访问");
    }

    // 返回无权限
    public static AjaxResp<Void> notAdmin() {
        return new AjaxResp<>(CODE_NOT_ADMIN, "不是管理员，没有权限");
    }

    // 返回一个自定义状态码的
    public static <T> AjaxResp<T> of(int code, String msg) {
        return new AjaxResp<>(code, msg);
    }
}
