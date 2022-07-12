package devilSpiderX.server.webServer.service;

import com.alibaba.fastjson2.JSONArray;

public interface MyPasswordsService {
    boolean add(String name, String account, String password, String remark, String owner);

    boolean delete(int id);

    boolean update(int id, String name, String account, String password, String remark);

    JSONArray query(String name, String owner);
}
