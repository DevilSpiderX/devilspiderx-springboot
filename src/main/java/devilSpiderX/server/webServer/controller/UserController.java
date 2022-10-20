package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
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
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
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
    private ResponseEntity<ResultMap<Void>> login(@RequestBody JSONObject reqBody, HttpSession session, WebRequest req) {
        ResultMap<Void> respResult = new ResultMap<>();
        HttpHeaders headers = new HttpHeaders();
        if (!reqBody.containsKey("uid")) {
            respResult.setCode(3);
            respResult.setMsg("uid参数不存在");
        } else if (!reqBody.containsKey("pwd")) {
            respResult.setCode(4);
            respResult.setMsg("pwd参数不存在");
        } else {
            String uid = reqBody.getString("uid");
            String pwd = reqBody.getString("pwd");
            User user = userService.get(uid);

            if (user == null) {
                respResult.setCode(2);
                respResult.setMsg("uid不存在");
            } else if (Objects.equals(user.getPassword().toLowerCase(), pwd.toLowerCase())) {
                session.setMaxInactiveInterval(SESSION_MAX_AGE);
                session.setAttribute("logged", true);
                session.setAttribute("uid", uid);

                ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId()).maxAge(SESSION_MAX_AGE)
                        .path("/").httpOnly(true).secure(req.isSecure()).build();
                headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

                respResult.setCode(0);
                respResult.setMsg("密码正确，登录成功");
            } else {
                respResult.setCode(1);
                respResult.setMsg("密码错误，登录失败");
            }
        }
        return ResponseEntity.ok().headers(headers).body(respResult);
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
    private ResultMap<Void> logout(HttpSession session) {
        ResultMap<Void> respResult = new ResultMap<>();
        try {
            session.removeAttribute("logged");
            session.removeAttribute("uid");
        } catch (IllegalStateException ignored) {
        }

        respResult.setCode(0);
        respResult.setMsg("登出成功");
        return respResult;
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
    private ResultMap<Void> register(@RequestBody JSONObject reqBody) {
        ResultMap<Void> respResult = new ResultMap<>();
        if (!reqBody.containsKey("uid")) {
            respResult.setCode(2);
            respResult.setMsg("uid参数不存在");
        } else if (!reqBody.containsKey("pwd")) {
            respResult.setCode(3);
            respResult.setMsg("pwd参数不存在");
        } else {
            String uid = reqBody.getString("uid");
            String pwd = SHA256(reqBody.getString("pwd"));

            if (userService.exist(uid)) {
                respResult.setCode(4);
                respResult.setMsg("该uid已存在");
            } else if (userService.register(uid, pwd)) {
                respResult.setCode(0);
                respResult.setMsg("注册成功");
            } else {
                respResult.setCode(1);
                respResult.setMsg("注册失败");
            }

        }
        return respResult;
    }

    /**
     * <b>状态</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 ；
     * </p>
     */
    @PostMapping("/status")
    @ResponseBody
    private ResultMap<Object> status(HttpSession session) {
        ResultMap<Object> respResult = new ResultMap<>();
        respResult.setCode(0);
        respResult.setMsg("OK");
        JSONObject respJson = new JSONObject();
        respJson.put("status", 0);
        respJson.put("login", false);
        respJson.put("uid", "");
        if (UserFilter.isLogged(session)) {
            respJson.put("status", 1);
            respJson.put("login", true);
            respJson.put("uid", session.getAttribute("uid"));
        }
        respResult.setData(respJson);
        return respResult;
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
