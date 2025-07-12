package devilSpiderX.server.webServer.module.user.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.annotation.GetPostMapping;
import devilSpiderX.server.webServer.core.resp.CommonResult;
import devilSpiderX.server.webServer.core.util.ClientIpUtil;
import devilSpiderX.server.webServer.module.user.model.dto.LoginDTO;
import devilSpiderX.server.webServer.module.user.model.dto.RegisterDTO;
import devilSpiderX.server.webServer.module.user.model.dto.UpdatePasswordDTO;
import devilSpiderX.server.webServer.module.user.model.vo.LoginVO;
import devilSpiderX.server.webServer.module.user.model.vo.StatusVO;
import devilSpiderX.server.webServer.module.user.model.vo.UploadAvatarVO;
import devilSpiderX.server.webServer.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "用户接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@EnableScheduling
public class UserController {

    private final UserService userService;

    @Operation(summary = "登录")
    @PostMapping("login")
    public CommonResult<LoginVO> login(
            final
            @Valid
            @RequestBody
            LoginDTO reqBody
    ) {
        final String username = reqBody.username();
        final String password = reqBody.password();

        final var loginVO = userService.login(username, password);
        userService.updateLastAddr(loginVO.getUid(), ClientIpUtil.getClientIp());

        return CommonResult.success(loginVO);
    }

    @Operation(summary = "登出")
    @PostMapping("logout")
    public CommonResult<Void> logout() {
        StpUtil.logout();
        return CommonResult.success();
    }


    @Operation(summary = "注册")
    @PostMapping("register")
    public CommonResult<Void> register(
            final
            @Valid
            @RequestBody
            RegisterDTO reqBody
    ) {
        userService.register(reqBody, ClientIpUtil.getClientIp());
        return CommonResult.success();
    }


    @Operation(summary = "用户状态")
    @GetPostMapping("status")
    public CommonResult<StatusVO> status() {
        final var result = new StatusVO();
        if (StpUtil.isLogin()) {
            result.setLogin(true);
            result.setUid(StpUtil.getLoginIdAsLong());
            result.setAdmin(StpUtil.hasRole("admin"));
            result.setRoles(StpUtil.getRoleList());
            result.setPermissions(StpUtil.getPermissionList());
        }
        return CommonResult.success(result);
    }

    @Operation(summary = "修改密码")
    @PostMapping("updatePassword")
    @SaCheckLogin
    public CommonResult<Void> updatePassword(
            final
            @Valid
            @RequestBody
            UpdatePasswordDTO reqBody
    ) {
        final var uid = StpUtil.getLoginIdAsLong();
        final var oldPassword = reqBody.oldPassword();
        final var newPassword = reqBody.newPassword();

        userService.updatePassword(uid, oldPassword, newPassword);

        return CommonResult.success();
    }

    public static final String userAvatarPrefix = "/user/avatar/";

    @Operation(summary = "上传头像")
    @PostMapping("uploadAvatar")
    @SaCheckLogin
    public CommonResult<UploadAvatarVO> uploadAvatar(
            final
            @Parameter(description = "用户头像文件")
            @RequestPart("image")
            MultipartFile imageFile
    ) {
        final var uid = StpUtil.getLoginIdAsLong();
        final String avatarName = userService.uploadAvatarImage(uid, imageFile);
        return CommonResult.success(new UploadAvatarVO(userAvatarPrefix + avatarName));
    }

    @Operation(summary = "获取头像地址")
    @GetMapping("avatar")
    @SaCheckLogin
    public CommonResult<String> getAvatar() {
        final String avatarName = userService.getAvatarImage(StpUtil.getLoginIdAsLong());
        if (avatarName == null) {
            return CommonResult.success("");
        }
        return CommonResult.success(userAvatarPrefix + avatarName);
    }
}
