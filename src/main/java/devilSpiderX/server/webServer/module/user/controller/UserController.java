package devilSpiderX.server.webServer.module.user.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.annotation.GetPostMapping;
import devilSpiderX.server.webServer.core.service.SettingsService;
import devilSpiderX.server.webServer.core.util.MyCipher;
import devilSpiderX.server.webServer.core.vo.AjaxResp;
import devilSpiderX.server.webServer.module.user.dto.LoginRequest;
import devilSpiderX.server.webServer.module.user.dto.RegisterRequest;
import devilSpiderX.server.webServer.module.user.dto.UpdatePasswordRequest;
import devilSpiderX.server.webServer.module.user.entity.User;
import devilSpiderX.server.webServer.module.user.service.UserService;
import devilSpiderX.server.webServer.module.user.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.IOException;
import java.util.Objects;

@Tag(name = "用户接口")
@RestController
@RequestMapping("/api/user")
@EnableScheduling
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final SettingsService settingsService;

    public UserController(UserService userService,
                          SettingsService settingsService) {
        this.userService = userService;
        this.settingsService = settingsService;
    }

    @Operation(summary = "登录")
    @PostMapping("login")
    public AjaxResp<LoginVo> login(
            @RequestBody LoginRequest reqBody,
            HttpServletRequest req
    ) {
        final String uid = reqBody.uid();
        final String password = reqBody.password();
        final User user = userService.get(uid);

        if (user == null) {
            return AjaxResp.success(LoginVo.of(2, "用户不存在"));
        } else if (Objects.equals(user.getPassword().toLowerCase(), password.toLowerCase())) {
            StpUtil.login(uid, settingsService.getSessionMaxAge());
            final SaSession session = StpUtil.getSession();
            session.set("user", user);

            var adminFlag = StpUtil.hasRole("admin");
            logger.info("{}{}登录成功", adminFlag ? "管理员" : "用户", uid);
            userService.updateLastAddr(uid, req.getRemoteAddr());
            return AjaxResp.success(LoginVo.of(0, "", new LoginDataVo(
                    uid,
                    StpUtil.getTokenValue(),
                    adminFlag,
                    StpUtil.getRoleList(),
                    StpUtil.getPermissionList(),
                    user.getLastAddress()
            )));
        } else {
            logger.info("{}输入密码错误，登录失败", uid);
            return AjaxResp.success(LoginVo.of(1, "密码错误"));
        }
    }

    @Operation(summary = "登出")
    @PostMapping("logout")
    public AjaxResp<Void> logout() {
        StpUtil.logout();
        return AjaxResp.success();
    }


    @Operation(summary = "注册")
    @PostMapping("register")
    public AjaxResp<RegisterVo> register(
            @RequestBody RegisterRequest reqBody,
            HttpServletRequest req
    ) {
        final String uid = reqBody.uid();
        final String password = reqBody.password();
        final String passwordSHA256 = MyCipher.bytes2Hex(MyCipher.SHA256(password));

        if (userService.exist(uid)) {
            return AjaxResp.success(new RegisterVo(2, "该uid已存在"));
        } else if (userService.register(uid, passwordSHA256, req.getRemoteAddr())) {
            return AjaxResp.success(new RegisterVo(0, ""));
        } else {
            return AjaxResp.success(new RegisterVo(1, "注册失败"));
        }
    }


    @Operation(summary = "用户状态")
    @GetPostMapping("status")
    public AjaxResp<StatusVo> status() {
        final var result = new StatusVo();
        if (StpUtil.isLogin()) {
            result.setLogin(true);
            result.setUid(StpUtil.getLoginIdAsString());
            result.setAdmin(StpUtil.hasRole("admin"));
            result.setRoles(StpUtil.getRoleList());
            result.setPermissions(StpUtil.getPermissionList());
        }
        return AjaxResp.success(result);
    }

    @Operation(summary = "修改密码")
    @PostMapping("updatePassword")
    @SaCheckLogin
    public AjaxResp<Void> updatePassword(@RequestBody UpdatePasswordRequest reqBody) {
        final String oldPassword = reqBody.oldPassword();
        final String newPassword = reqBody.newPassword();

        final String uid = StpUtil.getLoginIdAsString();
        final User user = userService.get(uid);

        if (Objects.equals(user.getPassword().toLowerCase(), oldPassword.toLowerCase())) {
            boolean flag = userService.updatePassword(uid, newPassword.toLowerCase());
            return flag ? AjaxResp.success() : AjaxResp.failure();
        } else {
            return AjaxResp.failure("旧密码错误");
        }
    }

    public static final String userAvatarPrefix = "/user/avatar/";

    @Operation(summary = "上传头像")
    @PostMapping("uploadAvatar")
    @SaCheckLogin
    public AjaxResp<UploadAvatarVo> uploadAvatar(
            @Parameter(description = "用户头像文件")
            @RequestPart("image") MultipartFile imageFile
    ) {
        final String uid = StpUtil.getLoginIdAsString();
        try {
            final String avatarName = userService.uploadAvatarImage(uid, imageFile);
            return AjaxResp.success(new UploadAvatarVo(userAvatarPrefix + avatarName));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return AjaxResp.error(e.getMessage());
        } catch (UnsupportedMediaTypeStatusException e) {
            return AjaxResp.error("上传的文件不是图片");
        }
    }

    @Operation(summary = "获取头像地址")
    @GetMapping("avatar")
    @SaCheckLogin
    public AjaxResp<String> getAvatar() {
        final String avatarName = userService.getAvatarImage(StpUtil.getLoginIdAsString());
        if (avatarName == null) {
            return AjaxResp.of(AjaxResp.success(), "");
        }
        return AjaxResp.of(AjaxResp.success(), userAvatarPrefix + avatarName);
    }
}
