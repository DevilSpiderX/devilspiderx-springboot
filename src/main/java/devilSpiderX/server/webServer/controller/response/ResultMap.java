package devilSpiderX.server.webServer.controller.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResultMap<V> implements Serializable, ResultBody<Map<String, V>> {
    @Serial
    private static final long serialVersionUID = -6491814391528291642L;
    private int code;
    private String msg;
    private Map<String, V> data = new HashMap<>();

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
    public Map<String, V> getData() {
        return data;
    }

    @Override
    public void setData(Map<String, V> data) {
        this.data = data;
    }

    public String[] keys() {
        Set<String> keySet = data.keySet();
        String[] keys = new String[keySet.size()];
        keySet.toArray(keys);
        return keys;
    }

    public V get(String key) {
        return data.get(key);
    }

    public void set(String key, V value) {
        data.put(key, value);
    }

    public void put(String key, V value) {
        data.put(key, value);
    }

    public void remove(String key) {
        data.remove(key);
    }

    public void remove(String key, V value) {
        data.remove(key, value);
    }

    public void clear() {
        data.clear();
    }

    public int size() {
        return data.size();
    }
}
