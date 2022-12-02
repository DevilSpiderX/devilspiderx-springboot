package devilSpiderX.server.webServer.controller;

import devilSpiderX.server.webServer.MainApplication;
import devilSpiderX.server.webServer.controller.response.ResultBody;
import devilSpiderX.server.webServer.controller.response.ResultData;
import devilSpiderX.server.webServer.service.OS;
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
    private ResultBody<?> OSReboot() {
        var respResult = new ResultData<>();
        respResult.setCode(0);
        respResult.setMsg("成功\n服务器正在重启......");
        OS.reboot(500);
        return respResult;
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
    private ResultBody<?> OSShutdown() {
        var respResult = new ResultData<>();
        respResult.setCode(0);
        respResult.setMsg("成功\n服务器正在关机......");
        OS.shutdown(500);
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
    private ResultBody<?> serviceShutdown() {
        var respResult = new ResultData<>();
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
        return respResult;
    }
}
