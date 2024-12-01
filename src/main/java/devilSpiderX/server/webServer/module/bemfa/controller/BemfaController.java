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

@Controller
@ConditionalOnBean(name = "mqttConnectOptions")
public class BemfaController implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(BemfaController.class);
    public static final long PERIOD = 30_000;

    private final BemfaMqttService mqttService;
    private final ServerInfoService serverInfoService;
    private final Thread.Builder taskThreadBuilder = Thread.ofVirtual().name("send-bemfa-mqtt-thread");
    private Thread taskThread;

    public BemfaController(BemfaMqttService mqttService, ServerInfoService serverInfoService) {
        this.mqttService = mqttService;
        this.serverInfoService = serverInfoService;
    }

    public void start() {
        if (taskThread != null && taskThread.isAlive()) {
            taskThread.interrupt();
            taskThread = null;
        }

        taskThread = taskThreadBuilder.start(taskRunnable);
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
            logger.error("MQTT连接没权限，将关闭任务");
            cancel();
            return;
        } catch (MqttException e) {
            logger.error("发送mqtt数据出现错误，下次发送数据将重连服务：{}(Code: {})", e.getMessage(), e.getReasonCode(), e);
            return;
        }
        lastTemperature = temperature;
        logger.debug("mqtt温度设置为：{}℃", temperature);
    }

    private final Runnable taskRunnable = new Runnable() {
        private final Object lock = new Object();

        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            final var currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted()) {
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
    };

}
