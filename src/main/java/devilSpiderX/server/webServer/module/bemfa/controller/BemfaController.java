package devilSpiderX.server.webServer.module.bemfa.controller;

import devilSpiderX.server.webServer.module.bemfa.service.BemfaMqttService;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Timer;
import java.util.TimerTask;

@Controller
public class BemfaController {
    public static final long PERIOD = 30_000;

    private final Logger logger = LoggerFactory.getLogger(BemfaController.class);
    private final BemfaMqttService mqttService;
    private final ServerInfoService serverInfoService;
    private final Timer senderTimer;

    public BemfaController(BemfaMqttService mqttService, ServerInfoService serverInfoService) {
        this.mqttService = mqttService;
        this.serverInfoService = serverInfoService;
        this.senderTimer = new Timer("send-bemfa-mqtt-thread", true);
        senderTimer.scheduleAtFixedRate(new Task(), 0, PERIOD);
    }

    public void cancel() {
        senderTimer.cancel();
    }

    private double lastTemperature = -1;

    public void task() {
        final var cpu = serverInfoService.getCPU();
        final var temperature = cpu.getTemperature();
        if (temperature == lastTemperature) return;

        mqttService.setData(temperature);
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
