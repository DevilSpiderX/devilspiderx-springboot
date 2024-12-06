package devilSpiderX.server.webServer.core.service.impl;

import devilSpiderX.server.webServer.DSXApplication;
import devilSpiderX.server.webServer.core.service.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class LinuxOS implements OS {
    private static final Logger logger = LoggerFactory.getLogger(LinuxOS.class);
    public static final String OS_NAME = "Linux";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public String getOSName() {
        return OS_NAME;
    }

    @Override
    public Charset getCharset() {
        return CHARSET;
    }

    @Override
    public void reboot(long millis) {
        List<String> rebootCMD = Arrays.asList("shutdown -r now".split(" "));
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                String result = system(rebootCMD);
                logger.info(result);
                DSXApplication.close();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }, "reboot").start();
    }

    @Override
    public void shutdown(long millis) {
        List<String> shutdownCMD = Arrays.asList("shutdown -h now".split(" "));
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                String result = system(shutdownCMD);
                logger.info(result);
                DSXApplication.close();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }, "shutdown").start();
    }

}
