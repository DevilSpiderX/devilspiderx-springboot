package devilSpiderX.server.webServer.module.serverInfo.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import devilSpiderX.server.webServer.core.resp.CommonResult;
import devilSpiderX.server.webServer.module.serverInfo.model.vo.*;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.serverInfo.statistic.CPU;
import devilSpiderX.server.webServer.module.serverInfo.statistic.CurrentOS;
import devilSpiderX.server.webServer.module.serverInfo.statistic.Disk;
import devilSpiderX.server.webServer.module.serverInfo.statistic.Memory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "系统软硬件信息接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ServerInfo")
@SaCheckLogin
public class ServerInfoController {

    private final ServerInfoService serverInfoService;

    @Operation(summary = "CPU信息")
    @GetMapping("cpu")
    public CommonResult<CPUVO> cpu() {
        CPU cpu = serverInfoService.getCPU();
        return CommonResult.success(serverInfoService.constructCpuObject(cpu));
    }

    @Operation(summary = "内存信息")
    @GetMapping("memory")
    public CommonResult<MemoryVO> memory() {
        Memory memory = serverInfoService.getMemory();
        return CommonResult.success(serverInfoService.constructMemoryObject(memory));
    }

    @Operation(summary = "网络信息")
    @GetMapping("networks")
    public CommonResult<List<NetworkVO>> networks() {
        final var networks = serverInfoService.getNetworks();
        final var networkDataList = new ArrayList<NetworkVO>(networks.length);
        for (var network : networks) {
            networkDataList.add(serverInfoService.constructNetworkObject(network));
        }
        return CommonResult.success(networkDataList);
    }

    @Operation(summary = "硬盘信息")
    @GetMapping("disks")
    public CommonResult<List<DiskVO>> disks() {
        var diskArray = new ArrayList<DiskVO>();
        for (Disk disk : serverInfoService.getDisks()) {
            diskArray.add(serverInfoService.constructDiskObject(disk));
        }
        return CommonResult.success(diskArray);
    }

    @Operation(summary = "系统信息")
    @GetMapping("os")
    public CommonResult<CurrentOSVO> os() {
        CurrentOS os = serverInfoService.getCurrentOS();
        return CommonResult.success(serverInfoService.constructCurrentOSObject(os));
    }
}
