package devilSpiderX.server.webServer.controller.response;

import java.io.Serializable;
import java.util.List;

public class ResultArray<T> implements Serializable {
    private static final long serialVersionUID = 7798393073388894464L;
    private int code;
    private String msg;
    private List<T> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
