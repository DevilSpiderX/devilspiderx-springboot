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
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class DSXRunner implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(DSXRunner.class);
    private final String pidFileName;

    public DSXRunner(DSXProperties dsxProperties) {
        pidFileName = dsxProperties.getPidFileName();
    }

    @Override
    public void run(ApplicationArguments args) {
        writePidFile();
    }

    private void writePidFile() {
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
        deletePidFile();
        logger.info("关闭服务器");
    }

    private void deletePidFile() {
        final var path = Paths.get(pidFileName);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error("删除pid文件出错", e);
        }
    }

}
