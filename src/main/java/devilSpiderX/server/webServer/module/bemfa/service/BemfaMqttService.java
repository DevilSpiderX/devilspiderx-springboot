package devilSpiderX.server.webServer.module.bemfa.service;

import devilSpiderX.server.webServer.module.bemfa.property.MqttProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@ConditionalOnBean(name = "mqttConnectOptions")
public class BemfaMqttService {
    private final MqttConnectOptions mqttConnectOptions;
    private final MqttProperties mqttProp;
    private IMqttClient mqttClient;

    public BemfaMqttService(final MqttConnectOptions mqttConnectOptions, final MqttProperties mqttProp) {
        this.mqttConnectOptions = mqttConnectOptions;
        this.mqttProp = mqttProp;
    }

    private IMqttClient connect() throws MqttException {
        final var client = new MqttClient(mqttProp.getUrl(), mqttProp.getClientId());
        client.connect(mqttConnectOptions);
        return client;
    }

    public void reconnect() throws MqttException {
        if (mqttClient != null) {
            mqttClient.disconnect();
            mqttClient.close();
            mqttClient = null;
        }

        mqttClient = connect();
    }

    public void setData(double temperature) throws MqttException, IOException {
        if (mqttClient == null) {
            mqttClient = connect();
        }

        final String text = "#%.2f".formatted(temperature);
        if (!mqttClient.isConnected()) {
            mqttClient.reconnect();
        }

        final var msg = new MqttMessage(text.getBytes());
        msg.setQos(0);
        mqttClient.publish(mqttProp.getTopic() + "/set", msg);
    }
}
