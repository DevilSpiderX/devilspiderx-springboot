package devilSpiderX.server.webServer.module.user.controller;

import devilSpiderX.server.webServer.core.service.SettingsService;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import devilSpiderX.server.webServer.core.util.MyCipher;
import devilSpiderX.server.webServer.module.user.entity.User;
import devilSpiderX.server.webServer.module.user.interceptor.LoginInterceptor;
import devilSpiderX.server.webServer.module.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "settingsService")
    private SettingsService settingsService;

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
    private ResponseEntity<AjaxResp<?>> login(@RequestBody LoginRequest reqBody, HttpServletRequest req) {
        if (reqBody.uid() == null || reqBody.pwd() == null) {
            return ResponseEntity.ok(AjaxResp.error());
        }
        String uid = reqBody.uid();
        String password = reqBody.password();
        User user = userService.get(uid);

        if (user == null) {
            return ResponseEntity.ok(AjaxResp.of(2, "用户不存在"));
        } else if (Objects.equals(user.getPassword().toLowerCase(), password.toLowerCase())) {
            HttpHeaders headers = new HttpHeaders();

            int SESSION_MAX_AGE = settingsService.getSessionMaxAge();

            HttpSession session = req.getSession();
            session.setMaxInactiveInterval(SESSION_MAX_AGE);
            session.setAttribute("uid", uid);
            session.setAttribute("user", user);

            ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId())
                    .maxAge(SESSION_MAX_AGE)
                    .path("/")
                    .httpOnly(true)
                    .secure(req.isSecure())
                    .build();
            headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

            var admin = userService.isAdmin(uid);
            logger.info("{}{}登录成功", admin ? "管理员" : "用户", uid);
            userService.updateLastAddr(uid, req.getRemoteAddr());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(AjaxResp.success(Map.of(
                            "uid", uid,
                            "admin", admin,
                            "lastLoginAddr", user.getLastAddress()
                    )));
        } else {
            logger.info("{}输入密码错误，登录失败", uid);
            return ResponseEntity.ok(AjaxResp.failure("密码错误"));
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
    private AjaxResp<?> logout(HttpSession session) {
        try {
            session.removeAttribute("uid");
            session.removeAttribute("user");
        } catch (IllegalStateException ignored) {
        }
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
    private AjaxResp<?> status(HttpSession session) {
        Map<String, Object> resultMap = new HashMap<>(Map.of(
                "login", false,
                "admin", false
        ));
        if (LoginInterceptor.isLogin(session)) {
            resultMap.put("login", true);
            User user = (User) session.getAttribute("user");
            resultMap.put("uid", user.getUid());
            resultMap.put("admin", user.getAdmin());
        }
        return AjaxResp.success(resultMap);
    }
}
