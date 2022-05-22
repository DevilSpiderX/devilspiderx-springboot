package devilSpiderX.server.webServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class OS {
    private static final Logger logger = LoggerFactory.getLogger(OS.class);

    public static String system(String... cmd) {
        StringBuilder resultBuilder = new StringBuilder();
        Process process = null;
        BufferedReader resultReader = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            process = builder.start();
            resultReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
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
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                String result = system("shutdown", "/r", "/t", "1", "/d", "p:4:1");
                logger.info(result);
                System.exit(0);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }, "reboot").start();
    }

    public static void shutdown(long millis) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                String result = system("shutdown", "/s", "/t", "1", "/d", "p:4:1");
                logger.info(result);
                System.exit(0);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }, "shutdown").start();
    }
}
