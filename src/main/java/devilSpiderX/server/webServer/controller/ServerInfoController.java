package devilSpiderX.server.webServer.controller;

import devilSpiderX.server.webServer.controller.response.ResultArray;
import devilSpiderX.server.webServer.controller.response.ResultBody;
import devilSpiderX.server.webServer.controller.response.ResultData;
import devilSpiderX.server.webServer.controller.response.ResultMap;
import devilSpiderX.server.webServer.service.ServerInfoService;
import devilSpiderX.server.webServer.statistics.CPU;
import devilSpiderX.server.webServer.statistics.Disk;
import devilSpiderX.server.webServer.statistics.Memory;
import devilSpiderX.server.webServer.statistics.Network;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
@RequestMapping("/api/ServerInfo")
public class ServerInfoController {
    @Resource(name = "serverInfoService")
    private ServerInfoService serverInfoService;

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
    private ResultBody<?> cpu() {
        var resultData = new ResultData<>();
        resultData.setCode(0);
        resultData.setMsg("获取成功");
        CPU cpu = serverInfoService.getCPU();
        resultData.setData(serverInfoService.constructCpuObject(cpu));
        return resultData;
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
    private ResultBody<?> memory() {
        var resultData = new ResultData<>();
        resultData.setCode(0);
        resultData.setMsg("获取成功");
        Memory memory = serverInfoService.getMemory();
        resultData.setData(serverInfoService.constructMemoryObject(memory));
        return resultData;
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
    private ResultBody<?> network() {
        var resultData = new ResultData<>();
        resultData.setCode(0);
        resultData.setMsg("获取成功");
        Network AllNet = new Network("All", 0, 0, 0);
        for (Network network : serverInfoService.getNetworks()) {
            AllNet.setUploadSpeed(AllNet.getUploadSpeed() + network.getUploadSpeed());
            AllNet.setDownloadSpeed(AllNet.getDownloadSpeed() + network.getDownloadSpeed());
        }
        resultData.setData(serverInfoService.constructNetworkObject(AllNet));
        return resultData;
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
    private ResultBody<?> disk() {
        var resultArray = new ResultArray<>();
        resultArray.setCode(0);
        resultArray.setMsg("获取成功");
        for (Disk disk : serverInfoService.getDisks()) {
            resultArray.add(serverInfoService.constructDiskObject(disk));
        }
        return resultArray;
    }

    public static String makeToken(String timeStr) {
        byte[] digest;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(timeStr.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
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
    private ResultBody<?> token() {
        var resultMap = new ResultMap<>();
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA));
        String token = makeToken(timeStr);
        if (token == null) {
            resultMap.setCode(1);
            resultMap.setMsg("token生成失败");
            return resultMap;
        }
        resultMap.setCode(0);
        resultMap.setMsg("成功");
        resultMap.set("token", token);
        resultMap.set("timeStr", timeStr);
        return resultMap;
    }
}
