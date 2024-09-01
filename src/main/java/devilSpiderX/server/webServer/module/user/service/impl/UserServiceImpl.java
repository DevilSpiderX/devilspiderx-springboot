package devilSpiderX.server.webServer.module.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import devilSpiderX.server.webServer.module.user.dao.UserMapper;
import devilSpiderX.server.webServer.module.user.entity.User;
import devilSpiderX.server.webServer.module.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User get(String uid) {
        if (uid == null) {
            return null;
        }
        final var wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getUid, uid);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public boolean register(String uid, String password, String ipAddr) {
        final var user = new User();
        user.setUid(uid);
        user.setPassword(password);
        user.setAdmin(false);
        user.setLastAddress(ipAddr);
        return userMapper.insert(user) > 0;
    }

    private final ReentrantLock isAdminLock = new ReentrantLock();

    @Override
    public boolean isAdmin(String uid) {
        if (uid == null) return false;
//        isAdminLock.lock();
        try {
            final var user = get(uid);
            return user != null && user.getAdmin();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
//            isAdminLock.unlock();
        }
    }

    @Override
    public boolean exist(String uid) {
        if (uid == null) {
            return false;
        }
        final var wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getUid, uid);
        return userMapper.exists(wrapper);
    }

    @Override
    public boolean updateLastAddr(String uid, String ipAddr) {
        if (uid == null || ipAddr == null) {
            return false;
        }
        final var wrapper = new LambdaUpdateWrapper<User>();
        wrapper.set(User::getLastAddress, ipAddr);
        wrapper.eq(User::getUid, uid);

        final int n = userMapper.update(wrapper);
        return n > 0;
    }

    @Override
    public boolean updatePassword(String uid, String password) {
        if (uid == null || password == null) {
            return false;
        }
        final var wrapper = new LambdaUpdateWrapper<User>();
        wrapper.set(User::getPassword, password);
        wrapper.eq(User::getUid, uid);

        final int n = userMapper.update(wrapper);
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

        final var wrapper = new LambdaUpdateWrapper<User>();
        wrapper.set(User::getAvatar, user.getAvatar());
        wrapper.eq(User::getUid, user.getUid());
        userMapper.update(wrapper);

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
