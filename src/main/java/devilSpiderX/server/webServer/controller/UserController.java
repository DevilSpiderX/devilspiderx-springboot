package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.filter.UserFilter;
import devilSpiderX.server.webServer.sql.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final SuidRich suidRich = BeeFactoryHelper.getSuidRich();
    private static final int SESSION_MAX_AGE = 10 * 60;

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
    @ResponseBody
    private JSONObject login(@RequestBody JSONObject reqBody, HttpSession session, HttpServletResponse resp) {
        JSONObject respJson = new JSONObject();
        if (!reqBody.containsKey("uid")) {
            respJson.put("code", "3");
            respJson.put("msg", "uid参数不存在");
        } else if (!reqBody.containsKey("pwd")) {
            respJson.put("code", "4");
            respJson.put("msg", "pwd参数不存在");
        } else {
            String uid = reqBody.getString("uid");
            String pwd = SHA256(reqBody.getString("pwd"));
            List<User> users = suidRich.select(new User(uid));

            boolean flag = false;
            for (User user : users) {
                if (pwd.equals(user.getPassword())) {
                    flag = true;
                    break;
                }
            }

            if (users.isEmpty()) {
                respJson.put("code", "2");
                respJson.put("msg", "uid不存在");
            } else if (flag) {
                session.setMaxInactiveInterval(SESSION_MAX_AGE);
                session.setAttribute("operable", true);
                session.setAttribute("uid", uid);
                Cookie jsessionid = new Cookie("JSESSIONID", session.getId());
                jsessionid.setMaxAge(SESSION_MAX_AGE);
                jsessionid.setPath("/");
                resp.addCookie(jsessionid);

                respJson.put("code", "0");
                respJson.put("msg", "密码正确，登录成功");
            } else {
                respJson.put("code", "1");
                respJson.put("msg", "密码错误，登录失败");
            }
        }
        return respJson;
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
    private JSONObject logout(HttpSession session, HttpServletResponse resp) {
        JSONObject respJson = new JSONObject();
        if (UserFilter.isOperable(session)) {
            Cookie jsessionid = new Cookie("JSESSIONID", session.getId());
            jsessionid.setMaxAge(1);
            jsessionid.setPath("/");
            resp.addCookie(jsessionid);
            session.invalidate();

            respJson.put("code", "0");
            respJson.put("msg", "登出成功");
        } else {
            respJson.put("code", "1");
            respJson.put("msg", "该sessionId（" + session.getId() + "）还未登录过");
        }
        return respJson;
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
    private JSONObject register(@RequestBody JSONObject reqBody) {
        JSONObject respJson = new JSONObject();
        if (!reqBody.containsKey("uid")) {
            respJson.put("code", "2");
            respJson.put("msg", "uid参数不存在");
        } else if (!reqBody.containsKey("pwd")) {
            respJson.put("code", "3");
            respJson.put("msg", "pwd参数不存在");
        } else {
            String uid = reqBody.getString("uid");
            String pwd = SHA256(reqBody.getString("pwd"));
            User user = new User(uid);

            if (suidRich.exist(user)) {
                respJson.put("code", "4");
                respJson.put("msg", "该uid已存在");
            } else {
                user.setPassword(pwd);
                user.setAdmin(false);
                if (suidRich.insert(user) > 0) {
                    respJson.put("code", "0");
                    respJson.put("msg", "注册成功");
                } else {
                    respJson.put("code", "1");
                    respJson.put("msg", "注册失败");
                }
            }
        }
        return respJson;
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
    private JSONObject status(HttpSession session) {
        JSONObject respJson = new JSONObject();
        respJson.put("status", 0);
        respJson.put("login", false);
        respJson.put("uid", "");
        if (UserFilter.isOperable(session)) {
            respJson.put("status", 1);
            respJson.put("login", true);
            respJson.put("uid", session.getAttribute("uid"));
        }
        return respJson;
    }

    private static String SHA256(String value) {
        StringBuilder resultBld = new StringBuilder();
        try {
            MessageDigest SHA256Digest = MessageDigest.getInstance("SHA-256");
            byte[] buff = SHA256Digest.digest(value.getBytes(StandardCharsets.UTF_8));
            for (byte b : buff) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    resultBld.append(0);
                }
                resultBld.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return resultBld.toString();
    }

}
