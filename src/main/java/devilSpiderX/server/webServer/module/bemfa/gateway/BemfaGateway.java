package devilSpiderX.server.webServer.module.bemfa.gateway;

import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.integration.annotation.MessagingGateway;

import java.io.IOException;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
@ConditionalOnBean(name = "outbound")
public interface BemfaGateway {
    void sendToMqtt(String data) throws MqttSecurityException, IOException;
}
