package devilSpiderX.server.webServer.module.user.controller;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.service.SettingsService;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import devilSpiderX.server.webServer.core.util.MyCipher;
import devilSpiderX.server.webServer.module.user.entity.User;
import devilSpiderX.server.webServer.module.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/api/user")
@EnableScheduling
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final SettingsService settingsService;

    public UserController(UserService userService,
                          SettingsService settingsService) {
        this.userService = userService;
        this.settingsService = settingsService;
    }

    /**
     * 登录请求参数
     *
     * @param uid 用户id
     * @param pwd 密码
     */
    record LoginRequest(String uid, String pwd) {
        /**
         * @return 密码
         */
        public String password() {
            return pwd;
        }
    }

    /**
     * <b>登录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link LoginRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 密码正确；1 密码错误；2 uid不存在；3 uid参数不存在；4 pwd参数不存在；
     * </p>
     */
    @PostMapping("/login")
    @ResponseBody
    private AjaxResp<?> login(@RequestBody LoginRequest reqBody, HttpServletRequest req) {
        if (reqBody.uid() == null || reqBody.pwd() == null) {
            return AjaxResp.error();
        }
        String uid = reqBody.uid();
        String password = reqBody.password();
        User user = userService.get(uid);

        if (user == null) {
            return AjaxResp.of(2, "用户不存在");
        } else if (Objects.equals(user.getPassword().toLowerCase(), password.toLowerCase())) {
            StpUtil.login(uid, settingsService.getSessionMaxAge());
            SaSession session = StpUtil.getSession();
            session.set("user", user);

            var admin = StpUtil.hasRole("admin");
            logger.info("{}{}登录成功", admin ? "管理员" : "用户", uid);
            userService.updateLastAddr(uid, req.getRemoteAddr());
            return AjaxResp.success(Map.of(
                    "uid", uid,
                    "admin", admin,
                    "roles", StpUtil.getRoleList(),
                    "lastLoginAddr", user.getLastAddress()
            ));
        } else {
            logger.info("{}输入密码错误，登录失败", uid);
            return AjaxResp.failure("密码错误");
        }
    }

    /**
     * <b>登出</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 登出成功；
     * </p>
     */
    @PostMapping("/logout")
    @ResponseBody
    private AjaxResp<?> logout() {
        StpUtil.logout();
        return AjaxResp.success();
    }

    /**
     * 注册请求参数
     *
     * @param uid 用户id
     * @param pwd 密码
     */
    record RegisterRequest(String uid, String pwd) {
        /**
         * @return 密码
         */
        public String password() {
            return pwd;
        }
    }

    /**
     * <b>注册</b>
     * <p>
     * <b>应包含参数：</b>
     * uid, pwd
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 注册成功；1 注册失败；2 uid参数不存在；3 pwd参数不存在；4 该uid已存在；
     * </p>
     */
    @PostMapping("/register")
    @ResponseBody
    private AjaxResp<?> register(@RequestBody RegisterRequest reqBody, HttpServletRequest req)
            throws NoSuchAlgorithmException {
        String uid = reqBody.uid();
        String password = reqBody.password();
        if (uid == null || password == null) {
            return AjaxResp.error();
        }
        password = MyCipher.bytes2Hex(MyCipher.SHA256(password));

        if (userService.exist(uid)) {
            return AjaxResp.of(2, "该uid已存在");
        } else if (userService.register(uid, password, req.getRemoteAddr())) {
            return AjaxResp.success();
        } else {
            return AjaxResp.failure();
        }
    }

    /**
     * <b>状态</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0: <code>
     * {
     * uid:string,
     * login:boolean,
     * admin:boolean
     * }
     * </code>；
     * </p>
     */
    @PostMapping("/status")
    @ResponseBody
    private AjaxResp<?> status() {
        Map<String, Object> resultMap = new HashMap<>(Map.of(
                "login", false,
                "admin", false
        ));
        if (StpUtil.isLogin()) {
            resultMap.put("login", true);
            resultMap.put("uid", StpUtil.getLoginId());
            resultMap.put("admin", StpUtil.hasRole("admin"));
        }
        return AjaxResp.success(resultMap);
    }
}
