package devilSpiderX.server.webServer.module.bemfa.service;

import devilSpiderX.server.webServer.module.bemfa.property.MqttProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@ConditionalOnBean(name = "mqttConnectOptions")
public class BemfaMqttService {
    private static final Logger logger = LoggerFactory.getLogger(BemfaMqttService.class);
    public static final int MQTT_TIME_TO_WAIT = 30_000;

    private final Lock lock = new ReentrantLock();
    private final MqttConnectOptions mqttConnectOptions;
    private final MqttProperties mqttProp;
    private IMqttClient mqttClient;
    private final String setTopic;
    private final MqttClientPersistence persistence = new MemoryPersistence();

    public BemfaMqttService(final MqttConnectOptions mqttConnectOptions, final MqttProperties mqttProp) {
        this.mqttConnectOptions = mqttConnectOptions;
        this.mqttProp = mqttProp;
        this.setTopic = mqttProp.getTopic() + "/set";
    }

    private void initMqttClient() throws MqttException {
        logger.info("client初始化中");
        lock.lock();
        try {
            if (this.mqttClient != null) {
                this.mqttClient.close();
                this.mqttClient = null;
            }
            final var mqttClient = new MqttClient(mqttProp.getUrl(), mqttProp.getClientId(), persistence);
            mqttClient.setTimeToWait(MQTT_TIME_TO_WAIT);
            this.mqttClient = mqttClient;
            logger.info("client初始化完成");
        } finally {
            lock.unlock();
        }
    }

    private void connect() throws MqttException {
        logger.info("mqtt服务连接中");
        lock.lock();
        try {
            if (mqttClient == null) {
                initMqttClient();
            }
            mqttClient.connect(mqttConnectOptions);
            logger.info("mqtt服务连接成功");
        } finally {
            lock.unlock();
        }
    }

    private int reconnectCount = 0;

    public void reconnect() throws MqttException {
        lock.lock();
        try {
            reconnectCount++;
            if (reconnectCount >= mqttProp.getMaxReconnectCount()) {
                logger.info("重连次数超过限制，弃用client，初始化新的client");
                reconnectCount = 0;
                initMqttClient();
            } else if (mqttClient != null) {
                logger.info("断开旧连接");
                try {
                    mqttClient.disconnect();
                    logger.info("断开旧连接成功");
                } catch (MqttException e) {
                    final var reasonCode = e.getReasonCode();
                    if (reasonCode == MqttException.REASON_CODE_CLIENT_ALREADY_DISCONNECTED) {
                        logger.info("client已经断开连接");
                    } else if (reasonCode == MqttException.REASON_CODE_CLIENT_CLOSED) {
                        logger.info("client已关闭，需要重新初始化新的client");
                        initMqttClient();
                    } else {
                        throw e;
                    }
                }
            }
            connect();
        } finally {
            lock.unlock();
        }
    }

    public void setData(double temperature) throws MqttException {
        lock.lock();
        try {
            if (mqttClient == null) {
                connect();
            }

            final String text = "#%.2f".formatted(temperature);
            if (!mqttClient.isConnected()) {
                logger.warn("mqtt连接失效，正在重连");
                reconnect();
            }

            final var msg = new MqttMessage(text.getBytes(StandardCharsets.UTF_8));
            msg.setQos(0);
            mqttClient.publish(setTopic, msg);
        } finally {
            lock.unlock();
        }
    }
}
