package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.listener.HttpSessionRegister;
import devilSpiderX.server.webServer.service.MyServerInfo;
import devilSpiderX.server.webServer.service.information.CPU;
import devilSpiderX.server.webServer.service.information.Disk;
import devilSpiderX.server.webServer.service.information.Memory;
import devilSpiderX.server.webServer.service.information.Network;
import devilSpiderX.server.webServer.util.WSSendTextThread;
import org.apache.tomcat.websocket.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@ServerEndpoint(value = "/websocket/getServerInfo/{token}/{timeStr}")
public class ServerInfoWS {
    private static final MyServerInfo serverInfo = MyServerInfo.serverInfo;
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoWS.class);
    private static final WSSendTextThread sendThread = new WSSendTextThread();
    private WsSession session;
    private String uid;
    private String address;
    private Thread sendServerInfoThread;

    static {
        sendThread.start();
    }

    @OnOpen
    public void onOpen(@PathParam("token") String token, @PathParam("timeStr") String timeStr, Session session)
            throws IOException {
        addOnlineCount();
        if (!token.equals(ServerInfoController.makeToken(timeStr))) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "token错误"));
            return;
        }
        this.session = (WsSession) session;
        HttpSession httpSession = HttpSessionRegister.getHttpSession(this.session.getHttpSessionId());
        uid = (String) httpSession.getAttribute("uid");
        address = (String) httpSession.getAttribute("address");
        info(address, "客户端" + uid + "接入");
        int onlineCount = getOnlineCount();
        info(address, "当前在线数量为：" + onlineCount);
    }

    @OnClose
    public void onClose(CloseReason reason) {
        if (sendServerInfoThread != null && !sendServerInfoThread.isInterrupted()) {
            sendServerInfoThread.interrupt();
        }
        subOnlineCount();
        info(address, "客户端" + uid + "退出 - " + reason.getCloseCode());
        int onlineCount = getOnlineCount();
        info(address, "当前在线数量为：" + onlineCount);
    }

    @OnError
    public void onError(Throwable error) {
        if (error instanceof java.io.EOFException) {
            logger.warn("java.io.EOFException");
            return;
        }
        logger.error(error.getMessage(), error);
    }

    @OnMessage
    public void onMessage(String msg) {
        info(address, "来自客户端" + uid + "的消息 - " + msg);
        JSONObject data = JSON.parseObject(msg);
        if ("start".equals(data.getString("cmd"))) {
            if (sendServerInfoThread != null) {
                sendServerInfoThread.interrupt();
            }
            sendServerInfoThread = new Thread(() -> sendServerInfo(data.getLong("cd")),
                    "send_ServerInfo_" + session.getId());
            sendServerInfoThread.start();
        }
    }

    public static int getOnlineCount() {
        return onlineCount.get();
    }

    public static void addOnlineCount() {
        onlineCount.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineCount.decrementAndGet();
    }

    public void sendMessage(String message) {
        sendThread.sendMessage(session, message);
    }

    public void sendServerInfo(long cd) {
        Thread currentThread = Thread.currentThread();
        while (!currentThread.isInterrupted()) {
            JSONObject data = new JSONObject();

            CPU cpu = serverInfo.update().getCPU();
            JSONObject cpuData = new JSONObject();
            cpuData.put("name", cpu.getName());
            cpuData.put("physicalNum", cpu.getPhysicalNum());
            cpuData.put("logicalNum", cpu.getLogicalNum());
            cpuData.put("usedRate", cpu.getUsedRate());
            cpuData.put("is64bit", cpu.is64bit());
            cpuData.put("cpuTemperature", cpu.getCpuTemperature());
            cpuData.put("freePercent", cpu.getFreePercent());
            cpuData.put("usedPercent", cpu.getUsedPercent());
            data.put("cpu", cpuData);

            Memory memory = serverInfo.update().getMemory();
            JSONObject memoryData = new JSONObject();
            memoryData.put("total", memory.getTotal());
            memoryData.put("used", memory.getUsed());
            memoryData.put("free", memory.getFree());
            memoryData.put("totalStr", memory.getTotalStr());
            memoryData.put("usedStr", memory.getUsedStr());
            memoryData.put("freeStr", memory.getFreeStr());
            memoryData.put("usage", memory.getUsage());
            data.put("memory", memoryData);

            JSONArray diskDataArray = new JSONArray();
            for (Disk disk : serverInfo.update().getDisks()) {
                JSONObject diskData = new JSONObject();
                diskData.put("label", disk.getLabel());
                diskData.put("mount", disk.getMount());
                diskData.put("fSType", disk.getFSType());
                diskData.put("name", disk.getName());
                diskData.put("total", disk.getTotal());
                diskData.put("free", disk.getFree());
                diskData.put("used", disk.getUsed());
                diskData.put("totalStr", disk.getTotalStr());
                diskData.put("freeStr", disk.getFreeStr());
                diskData.put("usedStr", disk.getUsedStr());
                diskData.put("usage", disk.getUsage());
                diskDataArray.add(diskData);
            }
            data.put("disk", diskDataArray);

            JSONArray networkArray = new JSONArray();
            for (Network network : serverInfo.update().getNetworks()) {
                JSONObject networkData = new JSONObject();
                networkData.put("name", network.getName());
                networkData.put("updateSpeed", network.getUpdateSpeed());
                networkData.put("downloadSpeed", network.getDownloadSpeed());
                networkData.put("IPv4addr", network.getIPv4addr());
                networkData.put("IPv6addr", network.getIPv6addr());
                networkData.put("updateSpeedStr", network.getUpdateSpeedStr());
                networkData.put("downloadSpeedStr", network.getDownloadSpeedStr());
                networkArray.add(networkData);
            }
            data.put("network", networkArray);

            sendMessage(data.toString());
            try {
                //noinspection BusyWait
                Thread.sleep(cd);
            } catch (InterruptedException e) {
                currentThread.interrupt();
            }
        }
    }

    private void info(String address, String msg) {
        if (address == null) return;
        logger.info("（{}） {}", address, msg);
    }
}
