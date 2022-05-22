package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import devilSpiderX.server.webServer.service.OS;
import devilSpiderX.server.webServer.service.V2ray;
import devilSpiderX.server.webServer.sql.MyPasswords;
import devilSpiderX.server.webServer.sql.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();

    public static boolean isOperable(HttpSession session) {
        return session.getAttribute("operable") != null && (Boolean) session.getAttribute("operable");
    }

    @GetMapping("/")
    public void welcome(HttpSession session, HttpServletResponse resp) throws IOException {
        if (isOperable(session)) {
            resp.sendRedirect("/index.html");
        } else {
            resp.sendRedirect("/login.html");
        }
    }

    /**
     * <b>命令请求，用于重启服务器和关机的命令</b>
     * <p>
     * <b>应包含参数：</b>
     * cmd
     * </p>
     * <b>返回代码：</b>
     * 0 成功；1 cmd的值不存在；2 cmd参数不存在；
     * 100 没有权限；101 没有管理员权限；
     * </p>
     */
    @PostMapping("/command")
    @ResponseBody
    private JSONObject command(@RequestBody JSONObject reqBody, HttpSession session) {
        JSONObject respJson = new JSONObject();
        if (isOperable(session)) {
            String cmdA = reqBody.getString("cmd");
            if (!User.isAdmin((String) session.getAttribute("uid"))) {
                respJson.put("code", "101");
                respJson.put("msg", "没有管理员权限");
            } else if (cmdA == null) {
                respJson.put("code", "2");
                respJson.put("msg", "cmd参数不存在");
            } else if (cmdA.equals("reboot")) {
                respJson.put("code", "0");
                respJson.put("msg", "成功\r\n服务器正在重启\r\n请稍后......");
                OS.reboot(500);
            } else if (cmdA.equals("shutdown")) {
                respJson.put("code", "0");
                respJson.put("msg", "成功\r\n服务器正在关机......");
                OS.shutdown(500);
            } else {
                respJson.put("code", "1");
                respJson.put("msg", "cmd的值不存在(\"reboot\"或\"shutdown\")");
            }
        } else {
            respJson.put("code", "100");
            respJson.put("msg", "没有权限，请登录");
        }
        return respJson;
    }

    /**
     * <b>测试POST请求</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；
     * </p>
     */
    @PostMapping("/test")
    @ResponseBody
    private JSONObject test() {
        JSONObject respJson = new JSONObject();
        respJson.put("code", "0");
        respJson.put("msg", "收到\n测试成功");
        return respJson;
    }

    /**
     * <b>查询保存的密码</b>
     * <p>
     * <b>应包含参数：</b>
     * key
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；1 空值；
     * 100 没有权限;
     * </p>
     */
    @PostMapping("/query")
    @ResponseBody
    private JSONObject queryPasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        JSONObject respJson = new JSONObject();
        if (isOperable(session)) {
            String key = "";
            if (reqBody.containsKey("key")) {
                key = reqBody.getString("key").trim();
            }
            JSONArray myPwdArray = MyPasswords.query(key, (String) session.getAttribute("uid"));
            if (myPwdArray.isEmpty()) {
                respJson.put("code", "1");
                respJson.put("msg", "空值");
            } else {
                respJson.put("code", "0");
                respJson.put("msg", "成功");
                respJson.put("data", myPwdArray);
            }
        } else {
            respJson.put("code", "100");
            respJson.put("msg", "没有权限，请登录");
        }
        return respJson;
    }

    /**
     * <b>v2ray的启动和关闭</b>
     * <p>
     * <b>应包含参数：</b>
     * cmd
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 启动成功；1 关闭成功；2 状态；3 cmd参数不存在；4 命令执行失败；
     * 100 没有权限；101 没有管理员权限；
     * </p>
     */
    @PostMapping("/v2ray")
    @ResponseBody
    private JSONObject v2ray(@RequestBody JSONObject reqBody, HttpSession session) throws IOException {
        JSONObject respJson = new JSONObject();
        if (isOperable(session)) {
            if (reqBody.containsKey("cmd")) {
                String command = reqBody.getString("cmd");
                if ((command.equals("start") || command.equals("stop")) &&
                        !User.isAdmin((String) session.getAttribute("uid"))) {
                    respJson.put("code", "101");
                    respJson.put("msg", "没有管理员权限");
                    return respJson;
                }
                if (command.equals("start") && !V2ray.isAlive()) {
                    long pid = V2ray.start();
                    logger.info("v2ray启动成功 PID=" + pid);
                    respJson.put("code", "0");
                    respJson.put("msg", "v2ray启动成功");
                } else if (command.equals("stop") && V2ray.isAlive()) {
                    String rValue = V2ray.stop();
                    rValue = rValue.replaceAll("\n", "\t");
                    logger.info(rValue);
                    respJson.put("code", "1");
                    respJson.put("msg", "v2ray关闭成功");
                } else if (command.equals("alive")) {
                    respJson.put("code", "2");
                    respJson.put("msg", V2ray.isAlive() ? "v2ray正在运行" : "v2ray没有运行");
                    respJson.put("state", V2ray.isAlive() ? 1 : 0);
                } else {
                    respJson.put("code", "4");
                    respJson.put("msg", "命令执行失败");
                }
            } else {
                respJson.put("code", "3");
                respJson.put("msg", "cmd参数不存在");
            }
        } else {
            respJson.put("code", "100");
            respJson.put("msg", "没有权限，请登录");
        }
        return respJson;
    }

    /**
     * <b>添加密码</b>
     * <p>
     * <b>应包含参数：</b>
     * name, account, password, remark
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 添加成功；1 添加失败；2 name参数不能为空或不存在； 100 没有权限；
     * </p>
     */
    @PostMapping("/addPasswords")
    @ResponseBody
    private JSONObject addPasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        JSONObject respJson = new JSONObject();
        if (isOperable(session)) {
            if (!reqBody.containsKey("name")) {
                respJson.put("code", "2");
                respJson.put("msg", "name参数不能为空或不存在");
            } else {
                String name = reqBody.getString("name");
                String account = reqBody.getString("account");
                String password = reqBody.getString("password");
                String remark = reqBody.getString("remark");
                MyPasswords newPwd = new MyPasswords();
                newPwd.setOwner((String) session.getAttribute("uid"));
                newPwd.setName(name);
                newPwd.setAccount(account);
                newPwd.setPassword(password);
                newPwd.setRemark(remark);
                if (newPwd.add()) {
                    respJson.put("code", "0");
                    respJson.put("msg", "添加成功");
                } else {
                    respJson.put("code", "1");
                    respJson.put("msg", "添加失败");
                }
            }
        } else {
            respJson.put("code", "100");
            respJson.put("msg", "没有权限，请登录");
        }
        return respJson;
    }

    /**
     * <b>修改密码</b>
     * <p>
     * <b>应包含参数：</b>
     * id, [name, account, password, remark]
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 修改成功；1 修改失败；2 id参数不能为空或不存在； 100 没有权限；
     * </p>
     */
    @PostMapping("/updatePasswords")
    @ResponseBody
    private JSONObject updatePasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        JSONObject respJson = new JSONObject();
        if (isOperable(session)) {
            if (!reqBody.containsKey("id")) {
                respJson.put("code", "2");
                respJson.put("msg", "id参数不能为空或不存在");
            } else {
                int id = reqBody.getInteger("id");
                String name = reqBody.getString("name");
                String account = reqBody.getString("account");
                String password = reqBody.getString("password");
                String remark = reqBody.getString("remark");
                MyPasswords pwd = new MyPasswords();
                pwd.setOwner((String) session.getAttribute("uid"));
                pwd.setId(id);
                pwd.setName(name);
                pwd.setAccount(account);
                pwd.setPassword(password);
                pwd.setRemark(remark);
                if (pwd.update()) {
                    respJson.put("code", "0");
                    respJson.put("msg", "修改成功");
                } else {
                    respJson.put("code", "1");
                    respJson.put("msg", "修改失败");
                }
            }

        } else {
            respJson.put("code", "100");
            respJson.put("msg", "没有权限，请登录");
        }
        return respJson;
    }

    /**
     * <b>关闭服务器程序</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 关闭成功； 100 没有权限；
     * </p>
     */
    @RequestMapping("/service/shutdown")
    @ResponseBody
    private JSONObject serviceShutdown(HttpSession session) {
        JSONObject respJson = new JSONObject();
        if (isOperable(session) && User.isAdmin((String) session.getAttribute("uid"))) {
            respJson.put("code", "0");
            respJson.put("msg", "关闭成功");
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    System.exit(0);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }, "service-shutdown-thread").start();
        } else {
            respJson.put("code", "100");
            respJson.put("msg", "没有权限，请登录管理员账号");
        }
        return respJson;
    }
}
