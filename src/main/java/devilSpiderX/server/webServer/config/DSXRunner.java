package devilSpiderX.server.webServer.config;

import devilSpiderX.server.webServer.MainApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

@Component
public class DSXRunner implements ApplicationRunner {
    //    private final MyConfig config;
    private final Logger logger = LoggerFactory.getLogger(DSXRunner.class);

    @Override
    public void run(ApplicationArguments args) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("WebServer.pid");
            writer.write(String.valueOf(ManagementFactory.getRuntimeMXBean().getPid()));
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(MainApplication::close, "shutdown_thread"));
    }
}
