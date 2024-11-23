package devilSpiderX.server.webServer.module.user.service;

import devilSpiderX.server.webServer.module.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface UserService {
    User get(String uid);

    boolean register(String uid, String password, String ipAddr);

    boolean isAdmin(String uid);

    boolean exist(String uid);

    boolean updateLastAddr(String uid, String ipAddr);

    boolean updatePassword(String uid, String password);

    String uploadAvatarImage(String uid, MultipartFile imageFile, Path avatarDirPath) throws IOException;

    String getAvatarImage(String uid);

    List<String> getUserPermissions(String uid);
}
