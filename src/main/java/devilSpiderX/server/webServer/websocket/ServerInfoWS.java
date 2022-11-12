package devilSpiderX.server.webServer.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.controller.ServerInfoController;
import devilSpiderX.server.webServer.listener.HttpSessionRegister;
import devilSpiderX.server.webServer.service.ServerInfoService;
import devilSpiderX.server.webServer.service.impl.ServerInfoServiceImpl;
import devilSpiderX.server.webServer.statistics.*;
import devilSpiderX.server.webServer.util.WSSendTextThread;
import org.apache.tomcat.websocket.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/websocket/getServerInfo/{token}/{timeStr}")
public class ServerInfoWS {
    private static final AtomicInteger onlineCount = new AtomicInteger();
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoWS.class);
    private static final ServerInfoService serverInfoService = new ServerInfoServiceImpl();
    private static final WSSendTextThread sender = new WSSendTextThread();
    private WsSession session;
    private String uid;
    private String address;
    private Thread timingSendInfoThread;

    static {
        sender.start();
    }

    @OnOpen
    public void onOpen(@PathParam("token") String token, @PathParam("timeStr") String timeStr, Session session)
            throws IOException {
        if (!token.equals(ServerInfoController.makeToken(timeStr))) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "token错误"));
            return;
        }
        this.session = (WsSession) session;
        HttpSession httpSession = HttpSessionRegister.getHttpSession(this.session.getHttpSessionId());
        uid = (String) httpSession.getAttribute("uid");
        address = (String) httpSession.getAttribute("address");
        info(address, "客户端" + uid + "接入");
        info(address, "当前在线数量为：" + onlineCount.incrementAndGet());
    }

    @OnClose
    public void onClose(CloseReason reason) {
        if (timingSendInfoThread != null && !timingSendInfoThread.isInterrupted()) {
            timingSendInfoThread.interrupt();
        }
        info(address, "客户端" + uid + "退出 - " + reason.getCloseCode());
        info(address, "当前在线数量为：" + onlineCount.decrementAndGet());
    }

    @OnError
    public void onError(Throwable error) {
        if (error instanceof java.io.EOFException) {
            logger.warn("java.io.EOFException");
            return;
        }
        logger.error("Websocket报错:" + error.getMessage(), error);
        logger.warn("isOpen? {}", session.isOpen());
    }

    @OnMessage
    public void onMessage(String msg) {
        info(address, "来自客户端" + uid + "的消息 - " + msg);
        JSONObject data = JSON.parseObject(msg);
        if ("start".equals(data.getString("cmd"))) {
            if (timingSendInfoThread != null) {
                timingSendInfoThread.interrupt();
            }
            timingSendInfoThread = new Thread(() -> sendServerInfo(data.getLong("cd")),
                    "timing_send_info_" + session.getId());
            timingSendInfoThread.start();
        }
    }

    public void sendMessage(String message) throws InterruptedException {
        sender.sendMessage(session, message);
    }


    public void sendServerInfo(long cd) {
        while (!Thread.interrupted()) {
            JSONObject data = new JSONObject();

            CPU cpu = serverInfoService.getCPU();
            data.put("cpu", serverInfoService.constructCpuObject(cpu));

            Memory memory = serverInfoService.getMemory();
            data.put("memory", serverInfoService.constructMemoryObject(memory));

            JSONArray diskDataArray = new JSONArray();
            List<Disk> disks = serverInfoService.getDisks();
            disks.sort(Comparator.naturalOrder());
            for (Disk disk : disks) {
                diskDataArray.add(serverInfoService.constructDiskObject(disk));
            }
            data.put("disk", diskDataArray);

            Network AllNet = new Network("All", 0, 0, 0);
            for (Network network : serverInfoService.getNetworks()) {
                AllNet.setUploadSpeed(AllNet.getUploadSpeed() + network.getUploadSpeed());
                AllNet.setDownloadSpeed(AllNet.getDownloadSpeed() + network.getDownloadSpeed());
            }
            data.put("network", serverInfoService.constructNetworkObject(AllNet));

            CurrentOS currentOS = serverInfoService.getCurrentOS();
            data.put("os", serverInfoService.constructCurrentOSObject(currentOS));

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
