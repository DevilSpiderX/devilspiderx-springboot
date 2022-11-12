package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.controller.response.ResultArray;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private ResultMap<Object> cpu() {
        ResultMap<Object> respResult = new ResultMap<>();
        respResult.setCode(0);
        respResult.setMsg("获取成功");
        CPU cpu = serverInfoService.getCPU();
        respResult.setData(serverInfoService.constructCpuObject(cpu));
        return respResult;
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
    private ResultMap<Object> memory() {
        ResultMap<Object> respResult = new ResultMap<>();
        respResult.setCode(0);
        respResult.setMsg("获取成功");
        Memory memory = serverInfoService.getMemory();
        respResult.setData(serverInfoService.constructMemoryObject(memory));
        return respResult;
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
    private ResultMap<Object> network() {
        ResultMap<Object> respResult = new ResultMap<>();
        respResult.setCode(0);
        respResult.setMsg("获取成功");
        Network AllNet = new Network("All", 0, 0, 0);
        for (Network network : serverInfoService.getNetworks()) {
            AllNet.setUploadSpeed(AllNet.getUploadSpeed() + network.getUploadSpeed());
            AllNet.setDownloadSpeed(AllNet.getDownloadSpeed() + network.getDownloadSpeed());
        }
        respResult.setData(serverInfoService.constructNetworkObject(AllNet));
        return respResult;
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
    private ResultArray<JSONObject> disk() {
        ResultArray<JSONObject> respResult = new ResultArray<>();
        respResult.setCode(0);
        respResult.setMsg("获取成功");
        List<JSONObject> dataArray = new LinkedList<>();
        for (Disk disk : serverInfoService.getDisks()) {
            dataArray.add(serverInfoService.constructDiskObject(disk));
        }
        respResult.setData(dataArray);
        return respResult;
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
    private ResultMap<String> token() {
        ResultMap<String> respResult = new ResultMap<>();
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA));
        String token = makeToken(timeStr);
        if (token == null) {
            respResult.setCode(1);
            respResult.setMsg("token生成失败");
            return respResult;
        }
        respResult.setCode(0);
        respResult.setMsg("成功");
        respResult.setData(Map.of("token", token, "timeStr", timeStr));
        return respResult;
    }
}
