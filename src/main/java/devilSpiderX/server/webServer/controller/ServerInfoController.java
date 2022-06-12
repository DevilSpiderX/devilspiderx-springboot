package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.service.MyServerInfo;
import devilSpiderX.server.webServer.service.information.CPU;
import devilSpiderX.server.webServer.service.information.Disk;
import devilSpiderX.server.webServer.service.information.Memory;
import devilSpiderX.server.webServer.service.information.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoController.class);
    private final SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
    private final MyServerInfo serverInfo = MyServerInfo.serverInfo;

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
    private JSONObject cpu() {
        JSONObject respJson = new JSONObject();
        respJson.put("code", "0");
        respJson.put("msg", "获取成功");
        JSONObject data = new JSONObject();
        CPU cpu = serverInfo.update().getCPU();
        data.put("name", cpu.getName());
        data.put("physicalNum", cpu.getPhysicalNum());
        data.put("logicalNum", cpu.getLogicalNum());
        data.put("usedRate", cpu.getUsedRate());
        data.put("is64bit", cpu.is64bit());
        data.put("cpuTemperature", cpu.getCpuTemperature());
        data.put("freePercent", cpu.getFreePercent());
        data.put("usedPercent", cpu.getUsedPercent());
        respJson.put("data", data);
        return respJson;
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
    private JSONObject memory() {

        JSONObject respJson = new JSONObject();
        respJson.put("code", "0");
        respJson.put("msg", "获取成功");
        JSONObject data = new JSONObject();
        Memory memory = serverInfo.update().getMemory();
        data.put("total", memory.getTotal());
        data.put("used", memory.getUsed());
        data.put("free", memory.getFree());
        data.put("totalStr", memory.getTotalStr());
        data.put("usedStr", memory.getUsedStr());
        data.put("freeStr", memory.getFreeStr());
        data.put("usage", memory.getUsage());
        respJson.put("data", data);
        return respJson;
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
    private JSONObject network() {
        JSONObject respJson = new JSONObject();
        respJson.put("code", "0");
        respJson.put("msg", "获取成功");
        JSONObject data = new JSONObject();
        Network AllNet = new Network("All", 0, 0, 0);
        for (Network network : serverInfo.update().getNetworks()) {
            AllNet.setUploadSpeed(AllNet.getUploadSpeed() + network.getUploadSpeed());
            AllNet.setDownloadSpeed(AllNet.getDownloadSpeed() + network.getDownloadSpeed());
        }
        data.put("uploadSpeed", AllNet.getUploadSpeed());
        data.put("downloadSpeed", AllNet.getDownloadSpeed());
        data.put("uploadSpeedStr", AllNet.getUploadSpeedStr());
        data.put("downloadSpeedStr", AllNet.getDownloadSpeedStr());
        respJson.put("data", data);
        return respJson;
    }

    /**
     * <b>网络数量</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；100 没有权限;
     * </p>
     */
    @PostMapping("/network/size")
    @ResponseBody
    private JSONObject network_size() {
        JSONObject respJson = new JSONObject();
        respJson.put("code", "0");
        respJson.put("msg", "获取成功");
        respJson.put("size", serverInfo.update().getNetworks().size());
        return respJson;
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
    private JSONObject disk() {

        JSONObject respJson = new JSONObject();
        respJson.put("code", "0");
        respJson.put("msg", "获取成功");
        JSONArray dataArray = new JSONArray();
        for (Disk disk : serverInfo.update().getDisks()) {
            JSONObject diskJson = new JSONObject();
            diskJson.put("label", disk.getLabel());
            diskJson.put("mount", disk.getMount());
            diskJson.put("fSType", disk.getFSType());
            diskJson.put("name", disk.getName());
            diskJson.put("total", disk.getTotal());
            diskJson.put("free", disk.getFree());
            diskJson.put("used", disk.getUsed());
            diskJson.put("totalStr", disk.getTotalStr());
            diskJson.put("freeStr", disk.getFreeStr());
            diskJson.put("usedStr", disk.getUsedStr());
            diskJson.put("usage", disk.getUsage());
            dataArray.add(diskJson);
        }
        respJson.put("data", dataArray);
        return respJson;
    }

    /**
     * <b>硬盘数量</b>
     * <p>
     * <b>应包含参数：</b>
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；100 没有权限;
     * </p>
     */
    @PostMapping("/disk/size")
    @ResponseBody
    private JSONObject disk_size() {
        JSONObject respJson = new JSONObject();
        respJson.put("code", "0");
        respJson.put("msg", "获取成功");
        respJson.put("size", serverInfo.update().getDisks().size());
        return respJson;
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
     * 0 成功；100 没有权限;
     * </p>
     */
    @PostMapping("/token")
    @ResponseBody
    private JSONObject token() {

        JSONObject respJson = new JSONObject();
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA));
        String token = makeToken(timeStr);
        respJson.put("code", "0");
        respJson.put("msg", "成功");
        respJson.put("token", token);
        respJson.put("timeStr", timeStr);
        return respJson;
    }
}
