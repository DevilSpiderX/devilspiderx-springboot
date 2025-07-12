package devilSpiderX.server.webServer.module.user.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.util.SqlUtil;
import devilSpiderX.server.webServer.core.constant.DigestConstant;
import devilSpiderX.server.webServer.core.exception.BaseException;
import devilSpiderX.server.webServer.core.property.DSXProperties;
import devilSpiderX.server.webServer.core.resp.ResultCode;
import devilSpiderX.server.webServer.core.service.SettingService;
import devilSpiderX.server.webServer.core.util.DigestUtils;
import devilSpiderX.server.webServer.core.util.SimpleStringFormatter;
import devilSpiderX.server.webServer.core.util.ValidUtil;
import devilSpiderX.server.webServer.module.mybatisflex.UpdateChainExt;
import devilSpiderX.server.webServer.module.user.model.dto.RegisterDTO;
import devilSpiderX.server.webServer.module.user.model.entity.User;
import devilSpiderX.server.webServer.module.user.model.entity.UserPermission;
import devilSpiderX.server.webServer.module.user.model.mapper.UserMapper;
import devilSpiderX.server.webServer.module.user.model.mapper.UserPermissionMapper;
import devilSpiderX.server.webServer.module.user.model.vo.LoginVO;
import devilSpiderX.server.webServer.module.user.service.UserService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static devilSpiderX.server.webServer.module.user.model.entity.table.UserPermissionTableDef.USER_PERMISSION;
import static devilSpiderX.server.webServer.module.user.model.entity.table.UserTableDef.USER;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final ValidUtil validUtil;
    private final UserMapper userMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final SettingService settingsService;
    private final DSXProperties dsxProperties;
    private Path avatarDirPath;

    @PostConstruct
    private void init() {
        this.avatarDirPath = Paths.get(dsxProperties.getAvatarDirPath());
    }

    @Override
    public LoginVO login(final @Nonnull String username, final @Nonnull String password) {
        Assert.hasText(username, "用户名不能为空");
        Assert.hasText(password, "密码不能为空");

        final var user = QueryChain.of(userMapper)
                .where(USER.USERNAME.eq(username))
                .one();
        if (user == null) {
            throw new BaseException(ResultCode.UserNotExists);
        }

        final var uid = user.getId();

        if (!DigestUtils.verifyPassword(password, user.getPassword())) {
            logger.info("用户{}(uid={})输入密码错误，登录失败", username, uid);
            throw new BaseException(ResultCode.UserWrongPassword);
        }

        StpUtil.login(uid, settingsService.getSessionMaxAge());
        final SaSession session = StpUtil.getSession();
        session.set("user", user);

        final var adminFlag = StpUtil.hasRole("admin");
        logger.info("{}{}(uid={})登录成功", adminFlag ? "管理员" : "用户", username, uid);

        return new LoginVO(
                uid,
                StpUtil.getTokenValue(),
                user.getLastAddress()
        );
    }

    @Transactional
    @Override
    public void register(final @Nonnull RegisterDTO dto, final String ipAddr) {
        Assert.notNull(dto, "dto不能为null");
        validUtil.validate(dto);

        final var exists = QueryChain.of(userMapper)
                .where(USER.USERNAME.eq(dto.username()))
                .exists();
        if (exists) {
            throw new BaseException(ResultCode.UserExists);
        }

        final var user = new User();
        user.setUsername(dto.username());
        final var _password = DigestUtils.encryptPassword(dto.password());
        user.setPassword(_password);
        user.setAdmin(false);
        user.setLastAddress(ipAddr);
        final var n = userMapper.insert(user);
        if (!SqlUtil.toBool(n)) {
            logger.info("用户{}注册失败}", user.getUsername());
            throw new BaseException(ResultCode.UserRegisterFailure);
        }
        logger.info("用户{}注册成功:id={}", user.getUsername(), user.getId());
    }

    @Override
    public boolean isAdmin(final long uid) {
        try {
            final var user = userMapper.selectOneById(uid);
            return user != null && user.getAdmin();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    @Override
    public void updateLastAddr(final long uid, final String ipAddr) {
        if (ipAddr == null) {
            return;
        }

        final var flag = UpdateChainExt.of(userMapper)
                .set(USER.LAST_ADDRESS, ipAddr)
                .where(USER.ID.eq(uid))
                .update();
        if (!flag) {
            logger.info("用户uid:{}的最后登录地址更新失败", uid);
            throw new BaseException(ResultCode.UpdateLastAddrFailure);
        }
        logger.info("用户uid:{}的最后登录地址已更新", uid);
    }

    @Override
    public void updatePassword(
            final long uid,
            final @Nonnull String oldPassword,
            final @Nonnull String newPassword
    ) {
        Assert.hasText(oldPassword, "旧密码不能为空");
        Assert.hasText(newPassword, "新密码不能为空");
        if (newPassword.length() < DigestConstant.minPasswordLength) {
            throw new IllegalArgumentException(SimpleStringFormatter.format(
                    "新密码长度不能少于{}位",
                    DigestConstant.minPasswordLength
            ));
        }

        final var user = userMapper.selectOneById(uid);

        if (!DigestUtils.verifyPassword(oldPassword, user.getPassword())) {
            throw new BaseException(ResultCode.UserWrongPassword);
        }

        final var flag = UpdateChainExt.of(userMapper)
                .set(USER.PASSWORD, DigestUtils.encryptPassword(newPassword))
                .where(USER.ID.eq(uid))
                .update();

        if (!flag) {
            logger.info("用户uid:{}的密码修改失败", uid);
            throw new BaseException(ResultCode.UpdateUserPasswordFailure);
        }
        logger.info("用户uid:{}的密码已修改", uid);
    }

    @Transactional
    @Override
    public String uploadAvatarImage(final long uid, final @Nonnull MultipartFile imageFile) {
        Objects.requireNonNull(imageFile, "imageFile不能为null");

        final var user = userMapper.selectOneById(uid);
        final var lastPath = user.getAvatar();

        final var contentType = imageFile.getContentType();
        final var type = contentType == null ? MediaType.ALL : MediaType.parseMediaType(contentType);
        final var suffix = switch (type.getSubtype()) {
            case "jpeg" -> "jpg";
            case "png" -> "png";
            case "gif" -> "gif";
            default -> throw new BaseException(ResultCode.UploadAvatarFailure, "不是图片类型的文件");
        };
        final var fileName = "%s.%s".formatted(UUID.randomUUID(), suffix);
        final var savePath = avatarDirPath.resolve(fileName);

        try {
            Files.createDirectories(avatarDirPath);
            imageFile.transferTo(savePath);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new BaseException(ResultCode.UploadAvatarFailure, e.getMessage());
        }

        user.setAvatar(fileName);

        final var flag = UpdateChainExt.of(userMapper)
                .set(USER.AVATAR, user.getAvatar())
                .where(USER.ID.eq(user.getId()))
                .update();

        if (!flag) {
            logger.info("用户uid:{}的头像上传失败", uid);
        }

        if (lastPath != null) {
            try {
                Files.deleteIfExists(Paths.get(lastPath));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("用户uid:{}的头像已上传:{}", uid, fileName);
        return fileName;
    }

    @Override
    public String getAvatarImage(final long uid) {
        final var user = userMapper.selectOneById(uid);
        if (user == null) {
            return null;
        }

        final var avatarPath = user.getAvatar();
        if (StringUtils.isBlank(avatarPath)) {
            return null;
        }

        return avatarPath;
    }

    @Override
    public List<String> getUserPermissions(final long uid) {
        final var result = new ArrayList<String>();

        final var permissionList = QueryChain.of(userPermissionMapper)
                .where(USER_PERMISSION.UID.eq(uid))
                .list();
        for (final UserPermission permission : permissionList) {
            result.add(permission.getPermission());
        }

        return result;
    }
}
