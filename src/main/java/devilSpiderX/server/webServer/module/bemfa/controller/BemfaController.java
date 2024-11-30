package devilSpiderX.server.webServer.module.bemfa.controller;

import devilSpiderX.server.webServer.module.bemfa.service.BemfaMqttService;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@ConditionalOnBean(name = "mqttConnectOptions")
public class BemfaController implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(BemfaController.class);
    public static final long PERIOD = 30_000;

    private final BemfaMqttService mqttService;
    private final ServerInfoService serverInfoService;
    private Thread taskThread;

    public BemfaController(BemfaMqttService mqttService, ServerInfoService serverInfoService) {
        this.mqttService = mqttService;
        this.serverInfoService = serverInfoService;
    }

    public void start() {
        if (taskThread != null && taskThread.isAlive()) {
            taskThread.interrupt();
        }

        taskThread = new TaskThread();
        taskThread.start();
    }

    public void cancel() {
        if (taskThread != null && taskThread.isAlive()) {
            taskThread.interrupt();
            taskThread = null;
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        start();
        logger.info("bemfa mqtt定时任务开始");
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
        } catch (MqttException e) {
            logger.error(
                    "发送mqtt数据出现错误，将重连服务：%s".formatted(e.getMessage()),
                    e
            );
            try {
                mqttService.reconnect();
            } catch (MqttException ex) {
                logger.error(
                        "重连mqtt服务出现问题：%s".formatted(ex.getMessage()),
                        ex
                );
            }
        }
        lastTemperature = temperature;
        logger.debug("温度设置为：{}℃", temperature);
    }

    private class TaskThread extends Thread {
        final Object lock = new Object();

        public TaskThread() {
            super("send-bemfa-mqtt-thread");
            setDaemon(true);
        }

        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    _run();
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }

                try {
                    Thread.sleep(PERIOD);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        private void _run() {
            synchronized (lock) {
                task();
            }
        }
    }

}
