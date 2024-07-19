package devilSpiderX.server.webServer.module.bemfa.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface BemfaGateway {
    void sendToMqtt(String data);
}
