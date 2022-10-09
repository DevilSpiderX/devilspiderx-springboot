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
import java.util.Comparator;
import java.util.List;
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

    public void sendMessage(String message) throws InterruptedException {
        sendThread.sendMessage(session, message);
    }

    public void sendServerInfo(long cd) {
        while (!Thread.interrupted()) {
            JSONObject data = new JSONObject();

            CPU cpu = serverInfo.update().getCPU();
            data.put("cpu", serverInfo.constructCpuObject(cpu));

            Memory memory = serverInfo.update().getMemory();
            data.put("memory", serverInfo.constructMemoryObject(memory));

            JSONArray diskDataArray = new JSONArray();
            List<Disk> disks = serverInfo.update().getDisks();
            disks.sort(Comparator.naturalOrder());
            for (Disk disk : disks) {
                diskDataArray.add(serverInfo.constructDiskObject(disk));
            }
            data.put("disk", diskDataArray);

            Network AllNet = new Network("All", 0, 0, 0);
            for (Network network : serverInfo.update().getNetworks()) {
                AllNet.setUploadSpeed(AllNet.getUploadSpeed() + network.getUploadSpeed());
                AllNet.setDownloadSpeed(AllNet.getDownloadSpeed() + network.getDownloadSpeed());
            }
            data.put("network", serverInfo.constructNetworkObject(AllNet));

            try {
                sendMessage(data.toString());
                //noinspection BusyWait
                Thread.sleep(cd);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void info(String address, String msg) {
        if (address == null) return;
        logger.info("（{}） {}", address, msg);
    }

}
