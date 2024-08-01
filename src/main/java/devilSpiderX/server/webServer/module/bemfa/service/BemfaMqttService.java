package devilSpiderX.server.webServer.module.bemfa.service;

import devilSpiderX.server.webServer.module.bemfa.gateway.BemfaGateway;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@ConditionalOnBean(name = "outbound")
public class BemfaMqttService {
    private final BemfaGateway gateway;

    public BemfaMqttService(BemfaGateway gateway) {
        this.gateway = gateway;
    }

    public void setData(double temperature) throws MqttSecurityException, IOException {
        final String text = "#%.2f".formatted(temperature);
        gateway.sendToMqtt(text);
    }
}
