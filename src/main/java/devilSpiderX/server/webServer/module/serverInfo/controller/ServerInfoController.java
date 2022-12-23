package devilSpiderX.server.webServer.module.serverInfo.controller;

import devilSpiderX.server.webServer.core.util.AjaxResp;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.serverInfo.service.TokenService;
import devilSpiderX.server.webServer.module.serverInfo.statistic.CPU;
import devilSpiderX.server.webServer.module.serverInfo.statistic.Disk;
import devilSpiderX.server.webServer.module.serverInfo.statistic.Memory;
import devilSpiderX.server.webServer.module.serverInfo.statistic.Network;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/api/ServerInfo")
public class ServerInfoController {
    @Resource(name = "serverInfoService")
    private ServerInfoService serverInfoService;

    @Resource(name = "tokenService")
    private TokenService tokenService;

    /**
     * <b>CPU信息</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；100 没有权限;
     * </p>
     */
    @PostMapping("/cpu")
    @ResponseBody
    private AjaxResp<?> cpu() {
        CPU cpu = serverInfoService.getCPU();
        return AjaxResp.success(serverInfoService.constructCpuObject(cpu));
    }

    /**
     * <b>内存信息</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；100 没有权限;
     * </p>
     */
    @PostMapping("/memory")
    @ResponseBody
    private AjaxResp<?> memory() {
        Memory memory = serverInfoService.getMemory();
        return AjaxResp.success(serverInfoService.constructMemoryObject(memory));
    }

    /**
     * <b>网络信息</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；100 没有权限;
     * </p>
     */
    @PostMapping("/network")
    @ResponseBody
    private AjaxResp<?> network() {
        Network AllNet = new Network("All", 0, 0, 0);
        for (Network network : serverInfoService.getNetworks()) {
            AllNet.setUploadSpeed(AllNet.getUploadSpeed() + network.getUploadSpeed());
            AllNet.setDownloadSpeed(AllNet.getDownloadSpeed() + network.getDownloadSpeed());
        }
        return AjaxResp.success(serverInfoService.constructNetworkObject(AllNet));
    }

    /**
     * <b>硬盘信息</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；100 没有权限;
     * </p>
     */
    @PostMapping("/disk")
    @ResponseBody
    private AjaxResp<?> disk() {
        var diskArray = new ArrayList<>();
        for (Disk disk : serverInfoService.getDisks()) {
            diskArray.add(serverInfoService.constructDiskObject(disk));
        }
        return AjaxResp.success(diskArray);
    }

    /**
     * <b>获取token</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；1 token生成失败；100 没有权限;
     * </p>
     */
    @PostMapping("/token")
    @ResponseBody
    private AjaxResp<?> token(@SessionAttribute String uid) {
        String token = tokenService.create(uid);
        return token != null ?
                AjaxResp.success(Map.of(
                        "token", token
                ))
                :
                AjaxResp.failure("token生成失败");
    }
}
