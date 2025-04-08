package devilSpiderX.server.webServer.core.configuration;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DSXRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DSXRunner.class);

    @Override
    public void run(ApplicationArguments args) {
    }

    @PreDestroy
    public void destroy() {
        logger.info("关闭服务器");
    }
}
