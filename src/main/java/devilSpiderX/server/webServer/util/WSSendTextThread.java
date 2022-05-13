package devilSpiderX.server.webServer.util;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.tomcat.websocket.WsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WSSendTextThread extends Thread {
    private static int count = 0;
    private final BlockingQueue<Tuple2<WsSession, String>> msgQue = new LinkedBlockingQueue<>();
    private static final Logger logger = LoggerFactory.getLogger(WSSendTextThread.class);

    public WSSendTextThread() {
        super("WS_sendText_thread_" + (count++));
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Tuple2<WsSession, String> tu = msgQue.take();
                tu._1.getBasicRemote().sendText(tu._2);
            } catch (InterruptedException | IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void sendMessage(WsSession session, String message) {
        try {
            msgQue.put(Tuple.of(session, message));
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
}