package devilSpiderX.server.webServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PubYun extends Thread {
    private static final String pyPath = "pubyun.py";
    private static final int time = 20;//分钟
    private static final Logger logger = LoggerFactory.getLogger(PubYun.class);

    public PubYun() {
        super("pubyun-update-thread");
        setDaemon(true);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        File pyFile = new File(pyPath);
        while (!interrupted()) {
            if (!pyFile.exists() || pyFile.isDirectory()) {
                interrupt();
                break;
            }
            String result = OS.system("python", pyPath).replaceAll("[\r\n]+", "");
            logger.info(result);
            try {
                sleep(time * 60 * 1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                interrupt();
            }
        }
    }
}
