package devilSpiderX.server.webServer.module.query.service;

import devilSpiderX.server.webServer.core.vo.CommonPage;
import devilSpiderX.server.webServer.module.query.vo.MyPasswordsVo;

import java.util.List;

public interface MyPasswordsService {
    boolean add(String name, String account, String password, String remark, String owner);

    boolean delete(int id);

    boolean update(int id, String name, String account, String password, String remark);

    List<MyPasswordsVo> query(String name, String owner);

    List<MyPasswordsVo> query(String[] names, String owner);

    List<MyPasswordsVo> query(List<String> names, String owner);

    CommonPage<MyPasswordsVo> queryPaging(String name, int length, int page, String owner);

    CommonPage<MyPasswordsVo> queryPaging(String[] names, int length, int page, String owner);

    CommonPage<MyPasswordsVo> queryPaging(List<String> names, int length, int page, String owner);
}
