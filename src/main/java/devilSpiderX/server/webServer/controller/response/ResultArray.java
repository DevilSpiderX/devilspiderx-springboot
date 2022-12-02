package devilSpiderX.server.webServer.controller.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResultArray<T> implements Serializable, ResultBody<List<T>> {
    @Serial
    private static final long serialVersionUID = 7798393073388894464L;
    private int code;
    private String msg;
    private List<T> data = new ArrayList<>();

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
    public List<T> getData() {
        return data;
    }

    @Override
    public void setData(List<T> data) {
        this.data = data;
    }

    public T get(int index) {
        return data.get(index);
    }

    public void add(T value) {
        data.add(value);
    }

    public void addAll(Collection<? extends T> c) {
        data.addAll(c);
    }

    public void remove(T t) {
        data.remove(t);
    }

    public void remove(int index) {
        data.remove(index);
    }

    public void removeAll(Collection<T> c) {
        data.removeAll(c);
    }

    public void clear() {
        data.clear();
    }

    public int size() {
        return data.size();
    }
}
