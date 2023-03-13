package devilSpiderX.server.webServer.module.serverInfo.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.serverInfo.service.TokenService;
import devilSpiderX.server.webServer.module.serverInfo.statistic.*;
import devilSpiderX.server.webServer.module.user.entity.User;
import jakarta.websocket.CloseReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ServerInfoWSHandler extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(ServerInfoWSHandler.class);
    private final AtomicInteger onlineCount = new AtomicInteger();
    private final ServerInfoService serverInfoService;
    private final TokenService tokenService;
    private final Map<String, Attribute> attributeMap = new HashMap<>();

    public ServerInfoWSHandler(ServerInfoService serverInfoService, TokenService tokenService) {
        this.serverInfoService = serverInfoService;
        this.tokenService = tokenService;
    }

    record Attribute(String uid, User user, String address, String token) {
    }

    private final Map<String, Thread> timingSendInfoThreadMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Map<String, Object> map = session.getAttributes();
        String uid = (String) map.get("uid");
        User user = (User) map.get("user");
        String address = (String) map.get("address");
        String token = (String) map.get("token");
        attributeMap.put(session.getId(), new Attribute(uid, user, address, token));
        info(address, "客户端" + uid + "接入");
        info(address, "当前在线数量为：" + onlineCount.incrementAndGet());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        Thread timingSendInfoThread = timingSendInfoThreadMap.remove(sessionId);
        if (timingSendInfoThread != null && !timingSendInfoThread.isInterrupted()) {
            timingSendInfoThread.interrupt();
        }
        Attribute attr = attributeMap.remove(sessionId);
        tokenService.destroy(attr.uid(), attr.token());
        info(attr.address(), "客户端" + attr.uid() + "退出 - "
                             + CloseReason.CloseCodes.getCloseCode(status.getCode()));
        info(attr.address(), "当前在线数量为：" + onlineCount.decrementAndGet());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String msg = message.getPayload();
        String sessionId = session.getId();
        Attribute attr = attributeMap.get(sessionId);
        info(attr.address(), "来自客户端" + attr.uid() + "的消息 - " + msg);
        JSONObject data = JSON.parseObject(msg);
        if ("start".equals(data.getString("cmd"))) {
            Thread timingSendInfoThread = new Thread(
                    () -> sendServerInfo(session, data.getLong("cd")),
                    "timing_send_info_" + sessionId
            );
            Thread lastThread = timingSendInfoThreadMap.put(sessionId, timingSendInfoThread);
            if (lastThread != null) {
                lastThread.interrupt();
                logger.info("中止上个定时线程");
            }
            timingSendInfoThread.start();
        }
    }

    public void sendServerInfo(WebSocketSession session, long cd) {
        logger.info("定时线程任务开始");
        Attribute attr = attributeMap.get(session.getId());
        try {
            while (!Thread.currentThread().isInterrupted()) {
                JSONObject data = new JSONObject();

                CPU cpu = serverInfoService.getCPU();
                data.put("cpu", serverInfoService.constructCpuObject(cpu));

                Memory memory = serverInfoService.getMemory();
                data.put("memory", serverInfoService.constructMemoryObject(memory));

                JSONArray diskDataArray = new JSONArray();
                List<Disk> disks = serverInfoService.getDisks();
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
                    session.sendMessage(new TextMessage(data.toString()));
                } catch (IOException e) {
                    logger.error("定时线程发送信息报错{}", e.getClass().getName());
                    logger.error(e.getMessage(), e);
                }

                if (!tokenService.check(attr.uid(), attr.token())) {
                    session.close(CloseStatus.NORMAL.withReason("token已超时"));
                }
                //noinspection BusyWait
                Thread.sleep(cd);
            }
        } catch (InterruptedException ignore) {
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            logger.error("关闭WebSocket失败,uid: {} ,sessionId: {}", attr.uid(), session.getId());
        }
        logger.info("定时线程任务结束");
    }

    private void info(String address, String msg) {
        logger.info("（{}） {}", address, msg);
    }
}
