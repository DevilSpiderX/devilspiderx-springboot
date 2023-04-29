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
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ServerInfoWSHandler extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(ServerInfoWSHandler.class);
    private final AtomicInteger onlineCount = new AtomicInteger();
    private final ServerInfoService serverInfoService;
    private final TokenService tokenService;
    private final Map<String, Attribute> attributeMap = new HashMap<>();
    private final Timer senderTimer = new Timer();
    private final Map<String, TimerTask> sendTaskMap = new HashMap<>();

    public ServerInfoWSHandler(ServerInfoService serverInfoService, TokenService tokenService) {
        this.serverInfoService = serverInfoService;
        this.tokenService = tokenService;
    }

    record Attribute(String uid, User user, String address, String token) {
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        final String sessionId = session.getId();
        final Map<String, Object> map = session.getAttributes();
        final String uid = (String) map.get("uid");
        final User user = (User) map.get("user");
        final String address = (String) map.get("address");
        final String token = (String) map.get("token");
        attributeMap.put(sessionId, new Attribute(uid, user, address, token));
        info(address, "用户%s接入，客户端id为%s".formatted(uid, sessionId));
        info(address, "当前在线数量为：%d".formatted(onlineCount.incrementAndGet()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        final String sessionId = session.getId();
        final Attribute attr = attributeMap.remove(sessionId);
        tokenService.destroy(attr.uid(), attr.token());
        info(attr.address(), "客户端%s退出 - %s".formatted(
                sessionId,
                CloseReason.CloseCodes.getCloseCode(status.getCode())
        ));
        info(attr.address(), "当前在线数量为：%d".formatted(onlineCount.decrementAndGet()));
        final TimerTask task = sendTaskMap.remove(sessionId);
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        final String msg = message.getPayload();
        final String sessionId = session.getId();
        final Attribute attr = attributeMap.get(sessionId);
        info(attr.address(), "来自客户端%s的消息 - %s".formatted(sessionId, msg));
        final JSONObject data = JSON.parseObject(msg);
        if ("start".equals(data.getString("cmd"))) {
            logger.info("客户端{}开始定时任务", sessionId);
            final TimerTask task = new SendTask(session);
            final TimerTask lastTask = sendTaskMap.put(sessionId, task);
            if (lastTask != null) {
                logger.info("客户端{}中止上个定时任务", sessionId);
                lastTask.cancel();
            }
            senderTimer.scheduleAtFixedRate(task, 0, data.getLongValue("cd", 1500));
        } else if ("stop".equals(data.getString("cmd"))) {
            logger.info("客户端{}停止定时任务", sessionId);
            final TimerTask task = sendTaskMap.remove(sessionId);
            if (task != null) {
                task.cancel();
            }
        }
    }

    public JSONObject getServerInfo() {
        final JSONObject data = new JSONObject();

        final CPU cpu = serverInfoService.getCPU();
        data.put("cpu", serverInfoService.constructCpuObject(cpu));

        final Memory memory = serverInfoService.getMemory();
        data.put("memory", serverInfoService.constructMemoryObject(memory));

        final JSONArray diskDataArray = new JSONArray();
        final List<Disk> disks = serverInfoService.getDisks();
        for (Disk disk : disks) {
            diskDataArray.add(serverInfoService.constructDiskObject(disk));
        }
        data.put("disk", diskDataArray);

        final Network AllNet = new Network("All", 0, 0, 0);
        for (Network network : serverInfoService.getNetworks()) {
            AllNet.setUploadSpeed(AllNet.getUploadSpeed() + network.getUploadSpeed());
            AllNet.setDownloadSpeed(AllNet.getDownloadSpeed() + network.getDownloadSpeed());
        }
        data.put("network", serverInfoService.constructNetworkObject(AllNet));

        final CurrentOS currentOS = serverInfoService.getCurrentOS();
        data.put("os", serverInfoService.constructCurrentOSObject(currentOS));

        return data;
    }

    private void info(String address, String msg) {
        logger.info("（{}） {}", address, msg);
    }

    private class SendTask extends TimerTask {
        private final WeakReference<WebSocketSession> sessionRef;
        private int index = 0;

        public SendTask(WebSocketSession session) {
            this.sessionRef = new WeakReference<>(session);
        }

        @Override
        public void run() {
            final WebSocketSession session = sessionRef.get();
            if (session == null) {
                return;
            }
            final String sessionId = session.getId();
            final Attribute attr = attributeMap.get(sessionId);
            final JSONObject data = getServerInfo();
            data.put("index", index++);

            try {
                session.sendMessage(new TextMessage(data.toString()));
            } catch (IOException e) {
                logger.error("发送信息报错{}", e.getClass().getName());
                logger.error(e.getMessage(), e);
            }

            if (!tokenService.check(attr.uid(), attr.token())) {
                try {
                    session.close(CloseStatus.NORMAL.withReason("token已超时"));
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    logger.error("关闭WebSocket失败,uid: {} ,sessionId: {}", attr.uid(), sessionId);
                } finally {
                    sendTaskMap.get(sessionId).cancel();
                }
            }
        }
    }
}
