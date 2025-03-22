package devilSpiderX.server.webServer.module.serverInfo.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import devilSpiderX.server.webServer.core.vo.AjaxResp;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.serverInfo.statistic.CPU;
import devilSpiderX.server.webServer.module.serverInfo.statistic.CurrentOS;
import devilSpiderX.server.webServer.module.serverInfo.statistic.Disk;
import devilSpiderX.server.webServer.module.serverInfo.statistic.Memory;
import devilSpiderX.server.webServer.module.serverInfo.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "系统软硬件信息接口")
@RestController
@RequestMapping("/api/ServerInfo")
@SaCheckLogin
public class ServerInfoController {
    private final ServerInfoService serverInfoService;

    public ServerInfoController(ServerInfoService serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    @Operation(summary = "CPU信息")
    @GetMapping("cpu")
    public AjaxResp<CPUVo> cpu() {
        CPU cpu = serverInfoService.getCPU();
        return AjaxResp.success(serverInfoService.constructCpuObject(cpu));
    }

    @Operation(summary = "内存信息")
    @GetMapping("memory")
    public AjaxResp<MemoryVo> memory() {
        Memory memory = serverInfoService.getMemory();
        return AjaxResp.success(serverInfoService.constructMemoryObject(memory));
    }

    @Operation(summary = "网络信息")
    @GetMapping("networks")
    public AjaxResp<List<NetworkVo>> networks() {
        final var networks = serverInfoService.getNetworks();
        final var networkDataList = new ArrayList<NetworkVo>(networks.length);
        for (var network : networks) {
            networkDataList.add(serverInfoService.constructNetworkObject(network));
        }
        return AjaxResp.success(networkDataList);
    }

    @Operation(summary = "硬盘信息")
    @GetMapping("disks")
    public AjaxResp<List<DiskVo>> disks() {
        var diskArray = new ArrayList<DiskVo>();
        for (Disk disk : serverInfoService.getDisks()) {
            diskArray.add(serverInfoService.constructDiskObject(disk));
        }
        return AjaxResp.success(diskArray);
    }

    @Operation(summary = "系统信息")
    @GetMapping("os")
    public AjaxResp<CurrentOSVo> os() {
        CurrentOS os = serverInfoService.getCurrentOS();
        return AjaxResp.success(serverInfoService.constructCurrentOSObject(os));
    }
}
