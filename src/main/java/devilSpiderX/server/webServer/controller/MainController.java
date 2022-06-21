package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.MainApplication;
import devilSpiderX.server.webServer.controller.response.ResultArray;
import devilSpiderX.server.webServer.controller.response.ResultMap;
import devilSpiderX.server.webServer.service.OS;
import devilSpiderX.server.webServer.sql.MyPasswords;
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

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final SuidRich suidRich = BeeFactoryHelper.getSuidRich();

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
    private ResultMap<Void> command(@RequestBody JSONObject reqBody, HttpSession session) {
        ResultMap<Void> respResult = new ResultMap<>();
        String cmdA = reqBody.getString("cmd");
        if (!User.isAdmin((String) session.getAttribute("uid"))) {
            respResult.setCode(101);
            respResult.setMsg("没有管理员权限");
        } else if (cmdA == null) {
            respResult.setMsg("cmd参数不存在");
        } else if (cmdA.equals("reboot")) {
            respResult.setCode(0);
            respResult.setMsg("成功\n服务器正在重启\n请稍后......");
            OS.reboot(500);
        } else if (cmdA.equals("shutdown")) {
            respResult.setCode(0);
            respResult.setMsg("成功\n服务器正在关机......");
            OS.shutdown(500);
        } else {
            respResult.setCode(1);
            respResult.setMsg("cmd的值不存在(\"reboot\"或\"shutdown\")");
        }
        return respResult;
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
    private ResultArray<Object> queryPasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        ResultArray<Object> respResult = new ResultArray<>();
        String key = "";
        if (reqBody.containsKey("key")) {
            key = reqBody.getString("key").trim();
        }
        JSONArray myPwdArray = MyPasswords.query(key, (String) session.getAttribute("uid"));
        if (myPwdArray.isEmpty()) {
            respResult.setCode(1);
            respResult.setMsg("空值");
        } else {
            respResult.setCode(0);
            respResult.setMsg("成功");
            respResult.setData(myPwdArray);
        }
        return respResult;
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
    private ResultMap<Void> addPasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        ResultMap<Void> respResult = new ResultMap<>();
        if (!reqBody.containsKey("name")) {
            respResult.setCode(2);
            respResult.setMsg("name参数不能为空或不存在");
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
                respResult.setCode(0);
                respResult.setMsg("添加成功");
            } else {
                respResult.setCode(1);
                respResult.setMsg("添加失败");
            }
        }
        return respResult;
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
    private ResultMap<Void> updatePasswords(@RequestBody JSONObject reqBody, HttpSession session) {
        ResultMap<Void> respResult = new ResultMap<>();
        if (!reqBody.containsKey("id")) {
            respResult.setCode(2);
            respResult.setMsg("id参数不能为空或不存在");
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
                respResult.setCode(0);
                respResult.setMsg("修改成功");
            } else {
                respResult.setCode(1);
                respResult.setMsg("修改失败");
            }
        }
        return respResult;
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
    private ResultMap<Void> serviceShutdown(HttpSession session) {
        ResultMap<Void> respResult = new ResultMap<>();
        if (User.isAdmin((String) session.getAttribute("uid"))) {
            respResult.setCode(0);
            respResult.setMsg("关闭成功");
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    MainApplication.close();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }, "service-shutdown-thread").start();
        } else {
            respResult.setCode(101);
            respResult.setMsg("没有权限，请登录管理员账号");
        }
        return respResult;
    }
}
