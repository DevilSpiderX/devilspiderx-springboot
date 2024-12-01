package devilSpiderX.server.webServer.module.bemfa.configuration;

import devilSpiderX.server.webServer.module.bemfa.property.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    private final MqttProperties prop;

    public MqttConfig(MqttProperties prop) {
        this.prop = prop;
    }

    @Bean
    @ConditionalOnProperty({"mqtt.bemfa.topic", "mqtt.bemfa.client-id"})
    public MqttConnectOptions mqttConnectOptions() {
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{prop.getUrl()});
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(false);
        options.setCleanSession(true);
        return options;
    }

}
