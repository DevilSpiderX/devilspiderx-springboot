package devilSpiderX.server.webServer.config;

import devilSpiderX.server.webServer.frame.DSXTrayIcon;
import devilSpiderX.server.webServer.service.PubYun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DSXRunner implements ApplicationRunner {
    private final MyConfig config;
    private final Logger logger = LoggerFactory.getLogger(DSXRunner.class);

    public DSXRunner(MyConfig config) {
        this.config = config;
    }

    @Override
    public void run(ApplicationArguments args) {
        new PubYun().start();
        if (config.getTRAY_ICON()) {
            System.setProperty("java.awt.headless", "false");
            logger.info("初始化系统托盘中......");
            DSXTrayIcon trayIcon = new DSXTrayIcon(config);
            logger.info("添加系统托盘中......");
            trayIcon.addSystemTrip();
            logger.info("系统托盘运行正常");
        }
    }
}
