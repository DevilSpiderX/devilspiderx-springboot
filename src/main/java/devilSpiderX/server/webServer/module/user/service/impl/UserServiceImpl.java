package devilSpiderX.server.webServer.module.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import devilSpiderX.server.webServer.module.user.entity.User;
import devilSpiderX.server.webServer.module.user.service.UserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserServiceImpl implements UserService {
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();

    @Override
    public User get(String uid) {
        if (uid == null) {
            return null;
        }
        return suid.selectOne(new User(uid));
    }

    @Override
    public boolean register(String uid, String password, String ipAddr) {
        User user = new User();
        user.setUid(uid);
        user.setPassword(password);
        user.setAdmin(false);
        user.setLastAddress(ipAddr);
        return suid.insert(user) > 0;
    }

    @Override
    public boolean isAdmin(String uid) {
        if (uid == null) return false;
        User user = suid.selectOne(new User(uid));
        return user != null && user.getAdmin();
    }

    @Override
    public boolean exist(String uid) {
        if (uid == null) {
            return false;
        }
        return suid.exist(new User(uid));
    }

    @Override
    public boolean updateLastAddr(String uid, String ipAddr) {
        if (uid == null || ipAddr == null) {
            return false;
        }
        User user = new User(uid);
        user.setLastAddress(ipAddr);
        int n = suid.updateBy(user, "uid", IncludeType.INCLUDE_EMPTY);
        return n > 0;
    }

    @Override
    public boolean updatePassword(String uid, String password) {
        if (uid == null || password == null) {
            return false;
        }
        User user = new User(uid);
        user.setPassword(password);
        int n = suid.updateBy(user, "uid", IncludeType.EXCLUDE_BOTH);
        return n > 0;
    }

    @Override
    public String uploadAvatarImage(String uid, MultipartFile imageFile, Path avatarDirPath) throws IOException {
        if (uid == null) {
            throw new NullPointerException("uid不能为null");
        }

        User user = get(uid);
        final String lastPath = user.getAvatarPath();
        if (lastPath != null) {
            Files.deleteIfExists(Paths.get(lastPath));
        }

        final String contentType = imageFile.getContentType();
        final MediaType type = contentType == null ? MediaType.ALL : MediaType.parseMediaType(contentType);
        String suffix;
        switch (type.getSubtype()) {
            case "jpeg" -> suffix = "jpg";
            case "png" -> suffix = "png";
            default -> throw new UnsupportedMediaTypeStatusException("不是图片类型的文件");
        }
        final String fileName = "%s.%s".formatted(SaSecureUtil.md5(uid), suffix);
        final Path savePath = avatarDirPath.resolve(fileName);

        Files.createDirectories(avatarDirPath);

        imageFile.transferTo(savePath);

        user.setAvatarPath(savePath.toAbsolutePath().toString());
        suid.update(user, "avatarPath");

        return fileName;
    }

    @Override
    public Resource getAvatarImage(String uid) {
        User user = get(uid);
        if (user == null) {
            return null;
        }

        final String avatarPath = user.getAvatarPath();
        if (avatarPath == null || avatarPath.isEmpty()) {
            return null;
        }

        return new FileSystemResource(Paths.get(avatarPath));
    }
}
