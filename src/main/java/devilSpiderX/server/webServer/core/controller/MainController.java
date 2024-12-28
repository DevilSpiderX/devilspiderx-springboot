package devilSpiderX.server.webServer.core.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import devilSpiderX.server.webServer.DSXApplication;
import devilSpiderX.server.webServer.core.service.OS;
import devilSpiderX.server.webServer.core.vo.AjaxResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统主接口")
@RestController
@RequestMapping("/api/admin")
@SaCheckRole("admin")
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final OS os;

    public MainController(final OS os) {
        this.os = os;
    }

    @Operation(summary = "重启服务器")
    @PostMapping("os/reboot")
    @SaCheckPermission("system.reboot")
    private AjaxResp<Object> OSReboot() {
        os.reboot(500);
        return AjaxResp.success("成功,服务器正在重启");
    }

    @Operation(summary = "关闭服务器")
    @PostMapping("os/shutdown")
    @SaCheckPermission("system.shutdown")
    private AjaxResp<Object> OSShutdown() {
        os.shutdown(500);
        return AjaxResp.success("成功,服务器正在关机");
    }

    @Operation(summary = "关闭服务")
    @RequestMapping(
            value = "service/shutdown",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @SaCheckPermission("process.shutdown")
    private AjaxResp<Object> serviceShutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                DSXApplication.close();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }, "service-shutdown-thread").start();
        return AjaxResp.success("关闭成功");
    }
}
