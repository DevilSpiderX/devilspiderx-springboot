package devilSpiderX.server.webServer.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

@Component
public class DSXRunner implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(DSXRunner.class);

    @Override
    public void run(ApplicationArguments args) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("devilspiderx.pid");
            writer.write(String.valueOf(ManagementFactory.getRuntimeMXBean().getPid()));
            writer.write(System.lineSeparator());
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
    }

    @PreDestroy
    public void destroy() {
        logger.info("关闭服务器");
    }
}
