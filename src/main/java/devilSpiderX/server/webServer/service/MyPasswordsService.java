package devilSpiderX.server.webServer.service;

import com.alibaba.fastjson2.JSONArray;
import io.vavr.Tuple2;

import java.util.List;

public interface MyPasswordsService {
    boolean add(String name, String account, String password, String remark, String owner);

    boolean delete(int id);

    boolean update(int id, String name, String account, String password, String remark);

    JSONArray query(String name, String owner);

    JSONArray query(String[] names, String owner);

    JSONArray query(List<String> names, String owner);

    Tuple2<JSONArray, Integer> query(String name, int page, String owner);

    Tuple2<JSONArray, Integer> query(String[] names, int page, String owner);

    Tuple2<JSONArray, Integer> query(List<String> names, int page, String owner);
}
