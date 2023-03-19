package devilSpiderX.server.webServer.module.user.service;

import devilSpiderX.server.webServer.module.user.entity.User;

public interface UserService {
    User get(String uid);

    boolean register(String uid, String password, String ipAddr);

    boolean isAdmin(String uid);

    boolean exist(String uid);

    boolean updateLastAddr(String uid, String ipAddr);

    boolean updatePassword(String uid, String password);
}
