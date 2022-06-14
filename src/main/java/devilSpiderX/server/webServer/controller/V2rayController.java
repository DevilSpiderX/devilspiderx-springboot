package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.service.V2ray;
import devilSpiderX.server.webServer.sql.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/api")
public class V2rayController {
    @Resource(name = "v2ray")
    private V2ray v2ray;
    private static final Logger logger = LoggerFactory.getLogger(V2rayController.class);

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
        if (reqBody.containsKey("cmd")) {
            String command = reqBody.getString("cmd");
            if ((command.equals("start") || command.equals("stop")) &&
                    !User.isAdmin((String) session.getAttribute("uid"))) {
                respJson.put("code", "101");
                respJson.put("msg", "没有管理员权限");
                return respJson;
            }
            if (command.equals("start") && !v2ray.isAlive()) {
                long pid = v2ray.start();
                logger.info("v2ray启动成功 PID=" + pid);
                respJson.put("code", "0");
                respJson.put("msg", "v2ray启动成功");
            } else if (command.equals("stop") && v2ray.isAlive()) {
                String rValue = v2ray.stop();
                rValue = rValue.replaceAll("\n", "\t");
                logger.info(rValue);
                respJson.put("code", "1");
                respJson.put("msg", "v2ray关闭成功");
            } else if (command.equals("alive")) {
                respJson.put("code", "2");
                respJson.put("msg", v2ray.isAlive() ? "v2ray正在运行" : "v2ray没有运行");
                respJson.put("state", v2ray.isAlive() ? 1 : 0);
            } else {
                respJson.put("code", "4");
                respJson.put("msg", "命令执行失败");
            }
        } else {
            respJson.put("code", "3");
            respJson.put("msg", "cmd参数不存在");
        }
        return respJson;
    }
}
