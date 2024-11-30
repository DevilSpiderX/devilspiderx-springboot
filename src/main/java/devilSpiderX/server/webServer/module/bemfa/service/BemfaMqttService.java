package devilSpiderX.server.webServer.module.bemfa.service;

import devilSpiderX.server.webServer.module.bemfa.property.MqttProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@ConditionalOnBean(name = "mqttConnectOptions")
public class BemfaMqttService {
    private static final Logger logger = LoggerFactory.getLogger(BemfaMqttService.class);

    private final MqttConnectOptions mqttConnectOptions;
    private final MqttProperties mqttProp;
    private IMqttClient mqttClient;

    public BemfaMqttService(final MqttConnectOptions mqttConnectOptions, final MqttProperties mqttProp) {
        this.mqttConnectOptions = mqttConnectOptions;
        this.mqttProp = mqttProp;
    }

    private void initMqttClient() throws MqttException {
        if (mqttClient != null) {
            mqttClient.close();
            mqttClient = null;
        }
        mqttClient = new MqttClient(mqttProp.getUrl(), mqttProp.getClientId());
    }

    private void connect() throws MqttException {
        if (mqttClient == null) {
            initMqttClient();
        }
        mqttClient.connect(mqttConnectOptions);
    }

    public void reconnect() throws MqttException {
        if (mqttClient != null) {
            mqttClient.disconnect();
        }
        connect();
    }

    public void setData(double temperature) throws MqttException {
        if (mqttClient == null) {
            connect();
        }

        final String text = "#%.2f".formatted(temperature);
        if (!mqttClient.isConnected()) {
            logger.warn("mqtt连接失效，正在重连");
            reconnect();
        }

        final var msg = new MqttMessage(text.getBytes());
        msg.setQos(0);
        mqttClient.publish(mqttProp.getTopic() + "/set", msg);
    }
}
