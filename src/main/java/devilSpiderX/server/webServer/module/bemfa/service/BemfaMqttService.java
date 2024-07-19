package devilSpiderX.server.webServer.module.bemfa.service;

import devilSpiderX.server.webServer.module.bemfa.gateway.BemfaGateway;
import org.springframework.stereotype.Service;

@Service
public class BemfaMqttService {
    private final BemfaGateway gateway;

    public BemfaMqttService(BemfaGateway gateway) {
        this.gateway = gateway;
    }

    public void setData(double temperature) {
        final String text = "#%.2f".formatted(temperature);
        gateway.sendToMqtt(text);
    }
}
