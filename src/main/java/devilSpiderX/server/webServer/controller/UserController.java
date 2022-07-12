package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.controller.response.ResultMap;
import devilSpiderX.server.webServer.filter.UserFilter;
import devilSpiderX.server.webServer.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

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
    private ResultMap<Void> login(@RequestBody JSONObject reqBody, HttpSession session, HttpServletResponse resp) {
        ResultMap<Void> respResult = new ResultMap<>();
        if (!reqBody.containsKey("uid")) {
            respResult.setCode(3);
            respResult.setMsg("uid参数不存在");
        } else if (!reqBody.containsKey("pwd")) {
            respResult.setCode(4);
            respResult.setMsg("pwd参数不存在");
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
                respResult.setCode(2);
                respResult.setMsg("uid不存在");
            } else if (flag) {
                session.setMaxInactiveInterval(SESSION_MAX_AGE);
                session.setAttribute("logged", true);
                session.setAttribute("uid", uid);

                respResult.setCode(0);
                respResult.setMsg("密码正确，登录成功");
            } else {
                respResult.setCode(1);
                respResult.setMsg("密码错误，登录失败");
            }
        }
        return respResult;
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
    private ResultMap<Void> logout(HttpSession session, HttpServletResponse resp) {
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
            User user = new User(uid);

            if (suidRich.exist(user)) {
                respResult.setCode(4);
                respResult.setMsg("该uid已存在");
            } else {
                user.setPassword(pwd);
                user.setAdmin(false);
                if (suidRich.insert(user) > 0) {
                    respResult.setCode(0);
                    respResult.setMsg("注册成功");
                } else {
                    respResult.setCode(1);
                    respResult.setMsg("注册失败");
                }
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
