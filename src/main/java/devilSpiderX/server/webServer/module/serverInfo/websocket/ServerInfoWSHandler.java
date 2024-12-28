package devilSpiderX.server.webServer.module.serverInfo.websocket;

import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.util.JacksonUtil;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.user.entity.User;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
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
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoWSHandler.class);

    private final AtomicInteger onlineCount = new AtomicInteger();
    private final ServerInfoService serverInfoService;
    private final Map<String, Attribute> attributeMap = new HashMap<>();
    private final Timer senderTimer = new Timer("send-server-info-thread", true);
    private final Map<String, TimerTask> sendTaskMap = new HashMap<>();

    public ServerInfoWSHandler(ServerInfoService serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    record Attribute(User user, String token) {
    }

    @OnOpen
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        final String sessionId = session.getId();
        final Map<String, Object> map = session.getAttributes();
        final User user = (User) map.get("user");
        final String token = (String) map.get("token");
        attributeMap.put(sessionId, new Attribute(user, token));
        logger.info("用户{}接入，当前在线数量为：{}", user.getUid(), onlineCount.incrementAndGet());
    }

    @OnClose
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        final String sessionId = session.getId();
        final Attribute attr = attributeMap.remove(sessionId);
        logger.info("用户{}退出，当前在线数量为：{} - {}{}",
                attr.user().getUid(),
                onlineCount.decrementAndGet(),
                CloseReason.CloseCodes.getCloseCode(status.getCode()),
                status.getReason() == null ? "" : " - %s".formatted(status.getReason())
        );
        final TimerTask task = sendTaskMap.remove(sessionId);
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * websocket获取的消息类型
     *
     * @param cmd 命令{@code [start, stop]}
     * @param cd  命令为start时，每次数据发送的间隔时长，单位为毫秒
     */
    record TextMsgData(String cmd, Long cd) {
        public long cd(long defaultValue) {
            return cd != null ? cd : defaultValue;
        }
    }

    @OnMessage
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        final var msg = message.getPayload();
        final var sessionId = session.getId();
        final Attribute attr = attributeMap.get(sessionId);
        final var uid = attr.user().getUid();
        logger.info("来自用户{}的消息 - {}", uid, msg);

        final var data = JacksonUtil.parseObject(msg, TextMsgData.class);

        if ("start".equals(data.cmd())) {
            logger.info("用户{}开始定时任务", uid);
            final TimerTask task = new SendTask(session);
            final TimerTask lastTask = sendTaskMap.put(sessionId, task);
            if (lastTask != null) {
                logger.info("用户{}中止上个定时任务", uid);
                lastTask.cancel();
            }
            senderTimer.scheduleAtFixedRate(task, 0, data.cd(1000));
        } else if ("stop".equals(data.cmd())) {
            logger.info("用户{}停止定时任务", uid);
            final TimerTask task = sendTaskMap.remove(sessionId);
            if (task != null) {
                task.cancel();
            }
        }
    }

    public Map<String, Object> getServerInfo() {
        final var data = new HashMap<String, Object>();

        final var cpu = serverInfoService.getCPU();
        data.put("cpu", serverInfoService.constructCpuObject(cpu));

        final var memory = serverInfoService.getMemory();
        data.put("memory", serverInfoService.constructMemoryObject(memory));

        final var diskDataList = new ArrayList<>();
        for (var disk : serverInfoService.getDisks()) {
            diskDataList.add(serverInfoService.constructDiskObject(disk));
        }
        data.put("disks", diskDataList);

        final var networks = serverInfoService.getNetworks();
        final var networkDataList = new ArrayList<>(networks.length);
        for (var network : networks) {
            networkDataList.add(serverInfoService.constructNetworkObject(network));
        }
        data.put("networks", networkDataList);

        final var currentOS = serverInfoService.getCurrentOS();
        data.put("os", serverInfoService.constructCurrentOSObject(currentOS));

        return data;
    }

    private class SendTask extends TimerTask {
        private final WeakReference<WebSocketSession> sessionRef;
        private int index = 0;

        public SendTask(WebSocketSession session) {
            this.sessionRef = new WeakReference<>(session);
        }

        @Override
        public void run() {
            try {
                _run();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        private void _run() {
            final var session = sessionRef.get();
            if (session == null || !session.isOpen()) {
                return;
            }
            final var sessionId = session.getId();
            final var attr = attributeMap.get(sessionId);
            final var data = getServerInfo();
            data.put("index", index++);

            try {
                session.sendMessage(new TextMessage(JacksonUtil.toJSONString(data)));
            } catch (IOException e) {
                logger.error("发送信息报错{}", e.getClass().getName(), e);
            }

            final var token = attr.token();
            final var timeout = StpUtil.getTokenTimeout(token);
            if (timeout == 0 || timeout == -2) {
                try {
                    session.close(CloseStatus.NORMAL.withReason("token已过期"));
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    logger.error("关闭WebSocket失败,uid: {} ,sessionId: {}", attr.user().getUid(), sessionId);
                }
            }
        }
    }
}
