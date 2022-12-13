package devilSpiderX.server.webServer.core.service.impl;

import devilSpiderX.server.webServer.MainApplication;
import devilSpiderX.server.webServer.core.service.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Service("WindowsOS")
public class WindowsOS implements OS {
    public static final String OS_NAME = "Windows";
    private static final Charset CHARSET = Charset.forName("GBK");
    private static final Logger logger = LoggerFactory.getLogger(WindowsOS.class);

    @Override
    public String getOSName() {
        return OS_NAME;
    }

    @Override
    public Charset getCharset() {
        return CHARSET;
    }

    @Override
    public String system(String... cmd) {
        return system(Arrays.asList(cmd));
    }

    @Override
    public void reboot(long millis) {
        List<String> rebootCMD = Arrays.asList("shutdown /r /t 1 /d p:4:1".split(" "));
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                String result = system(rebootCMD);
                logger.info(result);
                MainApplication.close();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }, "reboot").start();
    }

    @Override
    public void shutdown(long millis) {
        List<String> shutdownCMD = Arrays.asList("shutdown /s /t 1 /d p:4:1".split(" "));
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                String result = system(shutdownCMD);
                logger.info(result);
                MainApplication.close();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }, "shutdown").start();
    }
}
