package devilSpiderX.server.webServer.module.query.service;

import devilSpiderX.server.webServer.module.query.record.MyPasswordPagingResp;
import devilSpiderX.server.webServer.module.query.record.MyPasswordsResp;

import java.util.List;

public interface MyPasswordsService {
    boolean add(String name, String account, String password, String remark, String owner);

    boolean delete(int id);

    boolean update(int id, String name, String account, String password, String remark);

    List<MyPasswordsResp> query(String name, String owner);

    List<MyPasswordsResp> query(String[] names, String owner);

    List<MyPasswordsResp> query(List<String> names, String owner);

    MyPasswordPagingResp queryPaging(String name, int length, int page, String owner);

    MyPasswordPagingResp queryPaging(String[] names, int length, int page, String owner);

    MyPasswordPagingResp queryPaging(List<String> names, int length, int page, String owner);
}
