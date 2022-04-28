package devilSpiderX.server.webServer;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@ServletComponentScan
public class MainApplication {
    private static ConfigurableApplicationContext context = null;

    public static void main(String[] args) {
        context = SpringApplication.run(MainApplication.class, args);
    }

    public static void close() {
        LoggerFactory.getLogger(MainApplication.class).info("关闭服务器");
        int exitCode = SpringApplication.exit(MainApplication.context, () -> 0);
        System.exit(exitCode);
    }
}
