package devilSpiderX.server.webServer.module.bemfa.controller;

import devilSpiderX.server.webServer.module.bemfa.service.BemfaMqttService;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Controller
@ConditionalOnBean(name = "outbound")
public class BemfaController implements ApplicationRunner {
    public static final long PERIOD = 30_000;

    private final Logger logger = LoggerFactory.getLogger(BemfaController.class);
    private final BemfaMqttService mqttService;
    private final ServerInfoService serverInfoService;
    private final Timer senderTimer = new Timer("send-bemfa-mqtt-thread", true);

    public BemfaController(BemfaMqttService mqttService, ServerInfoService serverInfoService) {
        this.mqttService = mqttService;
        this.serverInfoService = serverInfoService;
    }

    public void cancel() {
        senderTimer.cancel();
    }

    @Override
    public void run(ApplicationArguments args) {
        senderTimer.scheduleAtFixedRate(new Task(), 0, PERIOD);
    }

    private double lastTemperature = -1;

    public void task() {
        final var cpu = serverInfoService.getCPU();
        final var temperature = cpu.getTemperature();
        if (temperature == lastTemperature) return;

        try {
            mqttService.setData(temperature);
        } catch (MqttSecurityException e) {
            logger.error("MQTT连接没权限，已关闭任务");
            cancel();
            return;
        } catch (IOException e) {
            logger.error("MQTT连接出现IO错误", e);
            return;
        }
        lastTemperature = temperature;
        logger.debug("温度设置为：{}℃", temperature);
    }

    private class Task extends TimerTask {

        @Override
        public void run() {
            try {
                _run();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        private void _run() {
            task();
        }
    }
}
