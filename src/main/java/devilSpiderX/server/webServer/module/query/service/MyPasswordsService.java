package devilSpiderX.server.webServer.module.query.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface MyPasswordsService {
    boolean add(String name, String account, String password, String remark, String owner);

    boolean delete(int id);

    boolean update(int id, String name, String account, String password, String remark);

    List<Map<String, Serializable>> query(String name, String owner);

    List<Map<String, Serializable>> query(String[] names, String owner);

    List<Map<String, Serializable>> query(List<String> names, String owner);

    record pageQueryRecord(List<Map<String, Serializable>> array, int pageCount) {
    }

    pageQueryRecord query(String name, int page, String owner);

    pageQueryRecord query(String[] names, int page, String owner);

    pageQueryRecord query(List<String> names, int page, String owner);
}
