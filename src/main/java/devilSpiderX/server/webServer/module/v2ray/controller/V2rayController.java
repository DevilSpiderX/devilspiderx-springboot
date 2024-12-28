package devilSpiderX.server.webServer.module.v2ray.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import devilSpiderX.server.webServer.core.vo.AjaxResp;
import devilSpiderX.server.webServer.module.v2ray.service.V2ray;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "V2ray接口")
@RestController
@RequestMapping("/api/admin/v2ray")
@SaCheckRole("admin")
public class V2rayController {
    private static final Logger logger = LoggerFactory.getLogger(V2rayController.class);

    private final V2ray v2ray;

    public V2rayController(V2ray v2ray) {
        this.v2ray = v2ray;
    }

    @Operation(summary = "启动v2ray")
    @PostMapping("start")
    private AjaxResp<Integer> start() {
        if (v2ray.isAlive()) {
            logger.info("v2ray正在运行");
            return AjaxResp.success("v2ray正在运行", 2);
        } else {
            try {
                long pid = v2ray.start();
                logger.info("v2ray启动成功 PID={}", pid);
                return AjaxResp.success("v2ray启动成功", 0);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return AjaxResp.success("v2ray启动失败", 1);
            }
        }
    }

    @Operation(summary = "关闭v2ray")
    @PostMapping("stop")
    private AjaxResp<Integer> stop() {
        if (v2ray.isAlive()) {
            if (v2ray.stop()) {
                logger.info("v2ray关闭成功");
                return AjaxResp.success("v2ray关闭成功", 0);
            } else {
                logger.info("v2ray关闭失败");
                return AjaxResp.success("v2ray关闭失败", 1);
            }
        } else {
            logger.info("v2ray没有运行");
            return AjaxResp.success("v2ray没有运行", 2);
        }
    }

    @Operation(summary = "v2ray的状态")
    @PostMapping("state")
    private AjaxResp<Boolean> state() {
        return AjaxResp.success(
                v2ray.isAlive() ? "v2ray正在运行" : "v2ray没有运行",
                v2ray.isAlive()
        );
    }

}
