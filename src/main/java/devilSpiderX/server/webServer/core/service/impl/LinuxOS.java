package devilSpiderX.server.webServer.core.service.impl;

import devilSpiderX.server.webServer.DSXApplication;
import devilSpiderX.server.webServer.core.service.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
        final var rebootCMD = "shutdown -r now".split(" ");
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
        final var shutdownCMD = "shutdown -h now".split(" ");
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
