package devilSpiderX.server.webServer.module.v2ray.controller;

import devilSpiderX.server.webServer.module.v2ray.service.V2ray;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("/api/admin/v2ray")
public class V2rayController {
    @Resource(name = "v2ray")
    private V2ray v2ray;
    private static final Logger logger = LoggerFactory.getLogger(V2rayController.class);

    /**
     * <b>启动v2ray</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 启动成功；1 启动失败；2 v2ray正在运行；
     * 100 没有权限；101 没有管理员权限；
     * </p>
     */
    @PostMapping("/start")
    @ResponseBody
    private AjaxResp<?> start() {
        if (v2ray.isAlive()) {
            logger.info("v2ray正在运行");
            return AjaxResp.of(2, "v2ray正在运行");
        } else {
            try {
                long pid = v2ray.start();
                logger.info("v2ray启动成功 PID={}", pid);
                return AjaxResp.success("v2ray启动成功");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return AjaxResp.failure("v2ray启动失败");
            }
        }
    }

    /**
     * <b>关闭v2ray</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 关闭成功；1 关闭失败；2 v2ray没有运行；
     * 100 没有权限；101 没有管理员权限；
     * </p>
     */
    @PostMapping("/stop")
    @ResponseBody
    private AjaxResp<?> stop() {
        if (v2ray.isAlive()) {
            if (v2ray.stop()) {
                logger.info("v2ray关闭成功");
                return AjaxResp.success("v2ray关闭成功");
            } else {
                logger.info("v2ray关闭失败");
                return AjaxResp.failure("v2ray关闭失败");
            }
        } else {
            logger.info("v2ray没有运行");
            return AjaxResp.of(2, "v2ray没有运行");
        }
    }

    /**
     * <b>v2ray的状态</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 v2ray没有运行；1 v2ray正在运行；
     * 100 没有权限；101 没有管理员权限；
     * </p>
     */
    @PostMapping("/state")
    @ResponseBody
    private AjaxResp<?> state() {
        return AjaxResp.success(
                v2ray.isAlive() ? "v2ray正在运行" : "v2ray没有运行",
                v2ray.isAlive()
        );
    }
}
