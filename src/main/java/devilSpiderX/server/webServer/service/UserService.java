package devilSpiderX.server.webServer.service;

import devilSpiderX.server.webServer.entity.User;

public interface UserService {
    User get(String uid);

    boolean register(String uid, String password, String ipAddr);

    boolean isAdmin(String uid);

    boolean exist(String uid);

    boolean updateLastAddr(String uid, String ipAddr);
}
