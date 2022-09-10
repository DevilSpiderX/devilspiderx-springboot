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
        SpringApplication app = new SpringApplication(MainApplication.class);
        context = app.run(args);
    }

    public static void close() {
        LoggerFactory.getLogger(MainApplication.class).info("关闭服务器");
        int code = SpringApplication.exit(MainApplication.context, () -> 0);
        System.exit(code);
    }
}
