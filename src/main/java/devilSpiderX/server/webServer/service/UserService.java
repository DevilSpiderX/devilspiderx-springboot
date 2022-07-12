package devilSpiderX.server.webServer.service;

public interface UserService {
    boolean isAdmin(String uid);

    boolean exist(String uid);
}
