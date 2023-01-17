package devilSpiderX.server.webServer.core.configuration;

import devilSpiderX.server.webServer.core.property.DSXProperties;
import jakarta.annotation.PreDestroy;
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
    private final Logger logger = LoggerFactory.getLogger(DSXRunner.class);
    private final String pidFileName;

    public DSXRunner(DSXProperties dsxProperties) {
        pidFileName = dsxProperties.getPidFileName();
    }

    @Override
    public void run(ApplicationArguments args) {
        try (FileWriter writer = new FileWriter(pidFileName)) {
            writer.write(String.valueOf(ManagementFactory.getRuntimeMXBean().getPid()));
            writer.write(System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @PreDestroy
    public void destroy() {
        logger.info("关闭服务器");
    }
}
