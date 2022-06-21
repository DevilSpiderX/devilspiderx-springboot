package devilSpiderX.server.webServer.service;

import devilSpiderX.server.webServer.MainApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


public class OS {
    public static final String OS_NAME;
    private static final String CHARSET_NAME;
    private static final Logger logger = LoggerFactory.getLogger(OS.class);

    static {
        String name = System.getProperty("os.name");
        if (name.startsWith("Windows")) {
            OS_NAME = "Windows";
            CHARSET_NAME = "GBK";
        } else if (name.startsWith("Linux")) {
            OS_NAME = "Linux";
            CHARSET_NAME = "UTF-8";
        } else {
            OS_NAME = "Other";
            CHARSET_NAME = "UTF-8";
        }
    }

    public static String system(String... cmd) {
        return system(Arrays.asList(cmd));
    }

    public static String system(List<String> cmd) {
        StringBuilder resultBuilder = new StringBuilder();
        Process process = null;
        BufferedReader resultReader = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            process = builder.start();
            resultReader = new BufferedReader(new InputStreamReader(process.getInputStream(), CHARSET_NAME));
            String line;
            while ((line = resultReader.readLine()) != null) {
                resultBuilder.append(line).append("\n");
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (resultReader != null) {
                    resultReader.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return resultBuilder.toString();
    }

    public static void reboot(long millis) {
        List<String> rebootCMD;
        switch (OS_NAME) {
            case "Windows": {
                rebootCMD = Arrays.asList("shutdown /r /t 1 /d p:4:1".split(" "));
                break;
            }
            case "Linux": {
                rebootCMD = Arrays.asList("shutdown -r now".split(" "));
                break;
            }
            default: {
                logger.warn("Unknown reboot command for this operating system");
                return;
            }
        }
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

    public static void shutdown(long millis) {
        List<String> shutdownCMD;
        switch (OS_NAME) {
            case "Windows": {
                shutdownCMD = Arrays.asList("shutdown /s /t 1 /d p:4:1".split(" "));
                break;
            }
            case "Linux": {
                shutdownCMD = Arrays.asList("shutdown -h now".split(" "));
                break;
            }
            default: {
                logger.warn("Unknown shutdown command for this operating system");
                return;
            }
        }
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
