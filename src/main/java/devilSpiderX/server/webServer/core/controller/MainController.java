package devilSpiderX.server.webServer.core.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import devilSpiderX.server.webServer.DSXApplication;
import devilSpiderX.server.webServer.core.annotation.GetPostMapping;
import devilSpiderX.server.webServer.core.resp.CommonResult;
import devilSpiderX.server.webServer.core.service.OS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统主接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@SaCheckRole("admin")
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final OS os;

    @Operation(summary = "重启服务器")
    @PostMapping("os/reboot")
    @SaCheckPermission("system.reboot")
    private CommonResult<Void> OSReboot() {
        os.reboot(500);
        return CommonResult.successMsg("成功,服务器正在重启");
    }

    @Operation(summary = "关闭服务器")
    @PostMapping("os/shutdown")
    @SaCheckPermission("system.shutdown")
    private CommonResult<Void> OSShutdown() {
        os.shutdown(500);
        return CommonResult.successMsg("成功,服务器正在关机");
    }

    @Operation(summary = "关闭服务")
    @GetPostMapping("service/shutdown")
    @SaCheckPermission("process.shutdown")
    private CommonResult<Void> serviceShutdown() {
        Thread.ofVirtual()
                .name("service-shutdown-thread")
                .start(() -> {
                    try {
                        Thread.sleep(1000);
                        DSXApplication.close();
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                });
        return CommonResult.successMsg("关闭成功");
    }
}
