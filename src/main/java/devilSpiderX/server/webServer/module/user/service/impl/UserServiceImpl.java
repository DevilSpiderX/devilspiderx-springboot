package devilSpiderX.server.webServer.module.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import devilSpiderX.server.webServer.module.user.entity.User;
import devilSpiderX.server.webServer.module.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.api.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
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
        final var user = new User();
        user.setUid(uid);
        user.setPassword(password);
        user.setAdmin(false);
        user.setLastAddress(ipAddr);
        return suid.insert(user) > 0;
    }

    private final ReentrantLock isAdminLock = new ReentrantLock();

    @Override
    public boolean isAdmin(String uid) {
        if (uid == null) return false;
        isAdminLock.lock();
        try {
            final var user = suid.selectOne(new User(uid));
            return user != null && user.getAdmin();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            isAdminLock.unlock();
        }
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
        final var user = new User(uid);
        user.setLastAddress(ipAddr);
        final int n = suid.updateBy(user, IncludeType.INCLUDE_EMPTY, "uid");
        return n > 0;
    }

    @Override
    public boolean updatePassword(String uid, String password) {
        if (uid == null || password == null) {
            return false;
        }
        final var user = new User(uid);
        user.setPassword(password);
        int n = suid.updateBy(user, IncludeType.EXCLUDE_BOTH, "uid");
        return n > 0;
    }

    @Override
    public String uploadAvatarImage(String uid, MultipartFile imageFile, Path avatarDirPath) throws IOException {
        if (uid == null) {
            throw new NullPointerException("uid不能为null");
        }

        final var user = get(uid);
        final var lastPath = user.getAvatar();
        if (lastPath != null) {
            Files.deleteIfExists(Paths.get(lastPath));
        }

        final var contentType = imageFile.getContentType();
        final var type = contentType == null ? MediaType.ALL : MediaType.parseMediaType(contentType);
        String suffix;
        switch (type.getSubtype()) {
            case "jpeg" -> suffix = "jpg";
            case "png" -> suffix = "png";
            case "gif" -> suffix = "gif";
            default -> throw new UnsupportedMediaTypeStatusException("不是图片类型的文件");
        }
        final var fileName = "%s.%s".formatted(SaSecureUtil.md5(uid), suffix);
        final var savePath = avatarDirPath.resolve(fileName);

        Files.createDirectories(avatarDirPath);

        imageFile.transferTo(savePath);

        user.setAvatar(fileName);
        suid.update(user, "avatar");

        return fileName;
    }

    @Override
    public String getAvatarImage(String uid) {
        final var user = get(uid);
        if (user == null) {
            return null;
        }

        final var avatarPath = user.getAvatar();
        if (avatarPath == null || avatarPath.isEmpty()) {
            return null;
        }

        return avatarPath;
    }
}
