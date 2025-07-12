package devilSpiderX.server.webServer.module.user.service;

import devilSpiderX.server.webServer.module.user.model.dto.RegisterDTO;
import devilSpiderX.server.webServer.module.user.model.vo.LoginVO;
import jakarta.annotation.Nonnull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    LoginVO login(@Nonnull String username, @Nonnull String password);

    void register(@Nonnull RegisterDTO dto, String ipAddr);

    boolean isAdmin(long uid);

    void updateLastAddr(long uid, String ipAddr);

    void updatePassword(long uid, @Nonnull String oldPassword, @Nonnull String newPassword);

    String uploadAvatarImage(long uid, @Nonnull MultipartFile imageFile);

    String getAvatarImage(long uid);

    List<String> getUserPermissions(long uid);

}
