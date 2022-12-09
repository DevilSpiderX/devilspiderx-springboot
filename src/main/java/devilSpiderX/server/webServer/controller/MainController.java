package devilSpiderX.server.webServer.controller;

import devilSpiderX.server.webServer.MainApplication;
import devilSpiderX.server.webServer.service.OS;
import devilSpiderX.server.webServer.util.AjaxResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/admin")
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    /**
     * <b>重启服务器</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <b>返回代码：</b>
     * 0 成功；100 没有权限；101 没有管理员权限；
     * </p>
     */
    @PostMapping("/os/reboot")
    @ResponseBody
    private AjaxResp<Void> OSReboot() {
        OS.reboot(500);
        return AjaxResp.success("成功,服务器正在重启");
    }

    /**
     * <b>关闭服务器</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <b>返回代码：</b>
     * 0 成功；100 没有权限；101 没有管理员权限；
     * </p>
     */
    @PostMapping("/os/shutdown")
    @ResponseBody
    private AjaxResp<Void> OSShutdown() {
        OS.shutdown(500);
        return AjaxResp.success("成功,服务器正在关机");
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
    private AjaxResp<Void> serviceShutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                MainApplication.close();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }, "service-shutdown-thread").start();
        return AjaxResp.success("关闭成功");
    }
}
