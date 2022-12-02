package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.controller.response.ResultBody;
import devilSpiderX.server.webServer.controller.response.ResultData;
import devilSpiderX.server.webServer.controller.response.ResultMap;
import devilSpiderX.server.webServer.entity.User;
import devilSpiderX.server.webServer.filter.UserFilter;
import devilSpiderX.server.webServer.service.SettingsService;
import devilSpiderX.server.webServer.service.UserService;
import devilSpiderX.server.webServer.util.MyCipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/api/user")
@EnableScheduling
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "settingsService")
    private SettingsService settingsService;
    private int SESSION_MAX_AGE = 0;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    private void pollingSettings() {
        int newMaxAge = Integer.parseInt(settingsService.get("session_max_age"));
        if (SESSION_MAX_AGE != newMaxAge) {
            SESSION_MAX_AGE = newMaxAge;
            logger.info("SESSION_MAX_AGE 设置为: {}s", SESSION_MAX_AGE);
        }
    }

    /**
     * <b>登录</b>
     * <p>
     * <b>应包含参数：</b>
     * uid, pwd
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 密码正确；1 密码错误；2 uid不存在；3 uid参数不存在；4 pwd参数不存在；
     * </p>
     */
    @PostMapping("/login")
    private ResponseEntity<ResultBody<?>> login(@RequestBody JSONObject reqBody, HttpSession session, HttpServletRequest req) {
        var resultMap = new ResultMap<>();
        HttpHeaders headers = new HttpHeaders();
        if (!reqBody.containsKey("uid")) {
            resultMap.setCode(3);
            resultMap.setMsg("uid参数不存在");
        } else if (!reqBody.containsKey("pwd")) {
            resultMap.setCode(4);
            resultMap.setMsg("pwd参数不存在");
        } else {
            String uid = reqBody.getString("uid");
            String pwd = reqBody.getString("pwd");
            User user = userService.get(uid);

            if (user == null) {
                resultMap.setCode(2);
                resultMap.setMsg("uid不存在");
            } else if (Objects.equals(user.getPassword().toLowerCase(), pwd.toLowerCase())) {
                session.setMaxInactiveInterval(SESSION_MAX_AGE);
                session.setAttribute("logged", true);
                session.setAttribute("uid", uid);

                ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId())
                        .maxAge(SESSION_MAX_AGE)
                        .path("/")
                        .httpOnly(true)
                        .secure(req.isSecure())
                        .build();
                headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

                resultMap.setCode(0);
                resultMap.setMsg("密码正确，登录成功");

                resultMap.set("uid", uid);
                resultMap.set("admin", userService.isAdmin(uid));
                resultMap.set("lastLoginAddr", user.getLastAddress());
                logger.info("{}{}登录成功", ((boolean) resultMap.get("admin")) ? "管理员" : "用户", uid);
                userService.updateLastAddr(uid, req.getRemoteAddr());
            } else {
                resultMap.setCode(1);
                resultMap.setMsg("密码错误，登录失败");
                logger.info("{}输入密码错误，登录失败", uid);
            }
        }
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resultMap);
    }

    /**
     * <b>登出</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 登出成功；1 未登录过；
     * </p>
     */
    @PostMapping("/logout")
    @ResponseBody
    private ResultBody<?> logout(HttpSession session) {
        var resultData = new ResultData<>();
        try {
            session.removeAttribute("logged");
            session.removeAttribute("uid");
        } catch (IllegalStateException ignored) {
        }
        resultData.setCode(0);
        resultData.setMsg("登出成功");
        return resultData;
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
    private ResultBody<?> register(@RequestBody JSONObject reqBody, HttpServletRequest req) {
        var resultData = new ResultData<>();
        if (!reqBody.containsKey("uid")) {
            resultData.setCode(2);
            resultData.setMsg("uid参数不存在");
        } else if (!reqBody.containsKey("pwd")) {
            resultData.setCode(3);
            resultData.setMsg("pwd参数不存在");
        } else {
            String uid = reqBody.getString("uid");
            String pwd = SHA256(reqBody.getString("pwd"));

            if (userService.exist(uid)) {
                resultData.setCode(4);
                resultData.setMsg("该uid已存在");
            } else if (userService.register(uid, pwd, req.getRemoteAddr())) {
                resultData.setCode(0);
                resultData.setMsg("注册成功");
            } else {
                resultData.setCode(1);
                resultData.setMsg("注册失败");
            }
        }
        return resultData;
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
    private ResultBody<?> status(HttpSession session) {
        var resultMap = new ResultMap<>();
        resultMap.setCode(0);
        resultMap.setMsg("OK");
        resultMap.put("login", false);
        if (UserFilter.isLogged(session)) {
            resultMap.put("login", true);
            String uid = Optional.of(session.getAttribute("uid")).orElse("").toString();
            resultMap.put("uid", uid);
            resultMap.put("admin", userService.isAdmin(uid));
        }
        return resultMap;
    }

    private static String SHA256(String value) {
        String result = null;
        try {
            result = MyCipher.SHA256(value);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

}
