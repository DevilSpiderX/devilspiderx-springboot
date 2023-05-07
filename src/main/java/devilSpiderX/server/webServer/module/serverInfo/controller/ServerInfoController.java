package devilSpiderX.server.webServer.module.serverInfo.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.serverInfo.service.TokenService;
import devilSpiderX.server.webServer.module.serverInfo.statistic.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/api/ServerInfo")
@SaCheckLogin
public class ServerInfoController {
    private final ServerInfoService serverInfoService;
    private final TokenService tokenService;

    public ServerInfoController(ServerInfoService serverInfoService, TokenService tokenService) {
        this.serverInfoService = serverInfoService;
        this.tokenService = tokenService;
    }

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
    @GetMapping("cpu")
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
    @GetMapping("memory")
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
    @GetMapping("networks")
    @ResponseBody
    private AjaxResp<?> networks() {
        final var networks = serverInfoService.getNetworks();
        final var networkDataList = new ArrayList<>(networks.length);
        final var AllNet = new Network("All");
        for (var network : networks) {
            AllNet.setBytesSent(AllNet.getBytesSent() + network.getBytesSent());
            AllNet.setBytesRecv(AllNet.getBytesRecv() + network.getBytesRecv());
            AllNet.setUploadSpeed(AllNet.getUploadSpeed() + network.getUploadSpeed());
            AllNet.setDownloadSpeed(AllNet.getDownloadSpeed() + network.getDownloadSpeed());
            networkDataList.add(serverInfoService.constructNetworkObject(network));
        }
        networkDataList.add(serverInfoService.constructNetworkObject(AllNet));
        return AjaxResp.success(networkDataList);
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
    @GetMapping("disks")
    @ResponseBody
    private AjaxResp<?> disks() {
        var diskArray = new ArrayList<>();
        for (Disk disk : serverInfoService.getDisks()) {
            diskArray.add(serverInfoService.constructDiskObject(disk));
        }
        return AjaxResp.success(diskArray);
    }

    /**
     * <b>系统信息</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；100 没有权限;
     * </p>
     */
    @GetMapping("os")
    @ResponseBody
    private AjaxResp<?> os() {
        CurrentOS os = serverInfoService.getCurrentOS();
        return AjaxResp.success(serverInfoService.constructCurrentOSObject(os));
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
    @GetMapping("token")
    @ResponseBody
    private AjaxResp<?> token() {
        String token = tokenService.create(StpUtil.getLoginIdAsString());
        return token != null ?
                AjaxResp.success(Map.of("token", token))
                :
                AjaxResp.failure("token生成失败");
    }
}
