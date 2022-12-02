package devilSpiderX.server.webServer.controller.response;

public interface ResultBody<T> {
    int getCode();

    void setCode(int code);

    String getMsg();

    void setMsg(String msg);

    T getData();

    void setData(T data);
}
