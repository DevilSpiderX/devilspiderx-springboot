package devilSpiderX.server.webServer.core.service.impl;

import devilSpiderX.server.webServer.DSXApplication;
import devilSpiderX.server.webServer.core.service.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class WindowsOS implements OS {
    private static final Logger logger = LoggerFactory.getLogger(WindowsOS.class);
    public static final String OS_NAME = "Windows";
    private static final Charset CHARSET = Charset.forName("GBK");

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
        final var rebootCMD = "shutdown /r /t 1 /d p:4:1".split(" ");
        Thread.ofVirtual()
                .name("reboot")
                .start(() -> {
                    try {
                        Thread.sleep(millis);
                        String result = system(rebootCMD);
                        logger.info(result);
                        DSXApplication.close();
                    } catch (InterruptedException e) {
                        Thread.currentThread()
                                .interrupt();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                });
    }

    @Override
    public void shutdown(long millis) {
        final var shutdownCMD = "shutdown /s /t 1 /d p:4:1".split(" ");
        Thread.ofVirtual()
                .name("shutdown")
                .start(() -> {
                    try {
                        Thread.sleep(millis);
                        String result = system(shutdownCMD);
                        logger.info(result);
                        DSXApplication.close();
                    } catch (InterruptedException e) {
                        Thread.currentThread()
                                .interrupt();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                });
    }

}
