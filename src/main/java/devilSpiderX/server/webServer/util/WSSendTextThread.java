package devilSpiderX.server.webServer.util;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.tomcat.websocket.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class WSSendTextThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(WSSendTextThread.class);
    private static final AtomicInteger count = new AtomicInteger();
    private final BlockingQueue<Tuple2<WsSession, String>> msgQueue = new LinkedBlockingQueue<>();

    public WSSendTextThread() {
        super("WS_send_text_thread_" + (count.get()));
        logger.info("实例化Websocket发送线程{}", count.getAndIncrement());
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Tuple2<WsSession, String> tu = msgQueue.take();
                if (tu._1.isOpen()) {
                    tu._1.getBasicRemote().sendText(tu._2);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void sendMessage(WsSession session, String message) throws InterruptedException {
        msgQueue.put(Tuple.of(session, message));
    }
}