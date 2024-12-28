package devilSpiderX.server.webServer.module.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import devilSpiderX.server.webServer.core.property.DSXProperties;
import devilSpiderX.server.webServer.module.user.dao.UserMapper;
import devilSpiderX.server.webServer.module.user.dao.UserPermissionMapper;
import devilSpiderX.server.webServer.module.user.entity.User;
import devilSpiderX.server.webServer.module.user.entity.UserPermission;
import devilSpiderX.server.webServer.module.user.service.UserService;
import jakarta.validation.constraints.NotNull;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final Path avatarDirPath;

    public UserServiceImpl(
            final UserMapper userMapper,
            final UserPermissionMapper userPermissionMapper,
            final DSXProperties dsxProperties
    ) {
        this.userMapper = userMapper;
        this.userPermissionMapper = userPermissionMapper;
        this.avatarDirPath = Paths.get(dsxProperties.getAvatarDirPath());
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

    @Override
    public boolean isAdmin(String uid) {
        if (uid == null) return false;
        try {
            final var user = get(uid);
            return user != null && user.getAdmin();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
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
    public String uploadAvatarImage(@NotNull String uid, MultipartFile imageFile) throws IOException {
        Objects.requireNonNull(uid, "uid不能为null");

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

    @Override
    public List<String> getUserPermissions(final String uid) {
        final var result = new ArrayList<String>();

        final var wrapper = Wrappers.lambdaQuery(UserPermission.class);
        wrapper.eq(UserPermission::getUid, uid);
        final var permissionList = userPermissionMapper.selectList(wrapper);
        for (final UserPermission permission : permissionList) {
            result.add(permission.getPermission());
        }

        return result;
    }
}
