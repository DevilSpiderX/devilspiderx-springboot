package devilSpiderX.server.webServer.controller.response;

import java.io.Serial;
import java.io.Serializable;

public class ResultData<T> implements Serializable, ResultBody<T> {
    @Serial
    private static final long serialVersionUID = -5724428261704938033L;
    private int code;
    private String msg;
    private T data;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }
}
